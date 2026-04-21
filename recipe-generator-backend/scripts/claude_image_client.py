#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Claude Code CLI Image Client
通过 Claude Code CLI 调用 MCP 的 understand_image 工具进行图像识别
"""
import sys
import json
import subprocess
import os
import shlex
import time
import base64
import re
import io

# Claude CLI 路径
CLAUDE_CLI = "C:/develop/Nodejs/node_cache/claude.cmd"


def normalize_base64_image(image_url: str) -> str:
    """
    将各种格式的base64图片标准化为纯base64 JPEG格式
    MiniMax API有时无法识别非标准格式的base64图片，需要重新编码
    """
    if not image_url.startswith("data:image"):
        return image_url

    # 解析 data:image/xxx;base64,xxxx 格式
    match = re.match(r'data:image/(\w+);base64,(.+)', image_url)
    if not match:
        return image_url

    mime_type = match.group(1).lower()
    encoded_data = match.group(2).strip()

    # 如果已经是标准jpeg或png格式，可能不需要转换
    if mime_type in ('jpeg', 'jpg', 'png'):
        # 但有时base64数据本身可能有问题，重新编码可以解决问题
        pass

    try:
        # 解码base64
        image_bytes = base64.b64decode(encoded_data)

        # 尝试用PIL重新编码为标准JPEG
        try:
            from PIL import Image
            img = Image.open(io.BytesIO(image_bytes))
            # 转换为RGB（如果是RGBA或其他模式）
            if img.mode != 'RGB':
                img = img.convert('RGB')
            # 重新编码为标准JPEG
            output = io.BytesIO()
            img.save(output, format='JPEG', quality=85)
            new_base64 = base64.b64encode(output.getvalue()).decode('ascii')
            return f"data:image/jpeg;base64,{new_base64}"
        except ImportError:
            # 没有PIL，直接返回原图（可能仍然有问题）
            print("警告: PIL未安装，无法标准化图片格式", file=sys.stderr)
            return image_url
        except Exception as e:
            print(f"图片重编码失败: {e}", file=sys.stderr)
            return image_url

    except Exception as e:
        print(f"Base64解码失败: {e}", file=sys.stderr)
        return image_url

def call_claude_image_understand(image_url: str, prompt: str = None, max_retries: int = 3) -> dict:
    """
    通过 Claude Code CLI 调用图像理解
    @param image_url: 图片URL或base64图片
    @param prompt: 可选的提示词
    @param max_retries: 最大重试次数
    @return: 识别结果
    """
    if prompt is None:
        prompt = "请识别这张图片中的食材，以JSON数组格式返回，每个元素包含：name(食材名称)、type(类别：蔬菜/肉类/蛋类/水产/豆制品/调味品/主食/水果/其他)。只返回JSON数组，不要其他文字。"

    # 构建完整的提示（包含图片）
    if image_url.startswith("data:image"):
        # base64图片直接放在提示中
        full_prompt = f"{prompt}\n图片：{image_url}"
    else:
        # URL图片
        full_prompt = f"{prompt}：{image_url}"

    for attempt in range(1, max_retries + 1):
        try:
            # 使用 --print 参数调用 Claude CLI
            result = subprocess.run(
                [CLAUDE_CLI, "--print", full_prompt],
                capture_output=True,
                encoding='utf-8',
                errors='replace',
                timeout=120,
                env={**os.environ, "CLAUDE_NO_AUTO_RETRY": "1"}
            )

            output = result.stdout.strip() if result.stdout else ""

            # 如果输出包含"无法访问"等错误信息，说明CLI无法处理
            if "无法访问" in output or "无法访问该URL" in output or "我没有办法" in output:
                return {"success": False, "error": "CLI无法访问该图片", "raw": output}

            # 尝试解析 JSON
            json_str = extract_json(output)

            if json_str:
                try:
                    foods = json.loads(json_str)
                    return {"success": True, "data": foods}
                except json.JSONDecodeError as e:
                    return {"success": False, "error": f"JSON解析失败: {e}", "raw": output}
            else:
                return {"success": False, "error": "未找到有效的JSON响应", "raw": output}

        except subprocess.TimeoutExpired:
            if attempt < max_retries:
                wait_time = attempt * 5
                time.sleep(wait_time)
                continue
            return {"success": False, "error": "调用超时（超过120秒）"}
        except FileNotFoundError:
            return {"success": False, "error": f"未找到 Claude CLI，请确保已安装 Claude Code（路径：{CLAUDE_CLI}）"}
        except Exception as e:
            return {"success": False, "error": str(e)}

    return {"success": False, "error": "重试次数耗尽"}


def extract_json(text: str) -> str:
    """从文本中提取 JSON 数组"""
    if not text:
        return ""

    text = text.strip()

    # 去除 markdown 代码块
    if "```json" in text:
        start = text.find("```json") + 7
        end = text.rfind("```")
        if end > start:
            return text[start:end].strip()
    elif "```" in text:
        start = text.find("```") + 3
        end = text.rfind("```")
        if end > start:
            return text[start:end].strip()

    # 查找 JSON 数组
    arr_start = text.find('[')
    arr_end = text.rfind(']')
    if arr_start >= 0 and arr_end > arr_start:
        return text[arr_start:arr_end + 1]

    # 查找 JSON 对象
    obj_start = text.find('{')
    obj_end = text.rfind('}')
    if obj_start >= 0 and obj_end > obj_start:
        return text[obj_start:obj_end + 1]

    return ""


def main():
    if len(sys.argv) < 2:
        print(json.dumps({"error": "用法: claude_image_client.py <image_url>"}))
        sys.exit(1)

    image_url = sys.argv[1]

    # 标准化base64图片格式
    normalized_url = normalize_base64_image(image_url)
    if normalized_url != image_url:
        print(f"图片已重新编码为标准JPEG格式", file=sys.stderr)

    result = call_claude_image_understand(normalized_url)
    print(json.dumps(result, ensure_ascii=False))


if __name__ == "__main__":
    main()
