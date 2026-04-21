#!/usr/bin/env python3
"""
MiniMax MCP Image Client
调用 MiniMax MCP 的 understand_image 工具进行图像识别
"""
import sys
import json
import subprocess
import os
import time
import threading

def read_output(process, results, timeout=120):
    """线程函数：读取stdout直到超时或收到响应"""
    start_time = time.time()
    lines = []
    while time.time() - start_time < timeout:
        # 检查进程是否还在运行
        if process.poll() is not None:
            break
        try:
            line = process.stdout.readline()
            if line:
                line_decoded = line.decode('utf-8', errors='ignore')
                lines.append(line_decoded)
                # 检查是否是我们需要的响应
                if '"result"' in line_decoded or '"error"' in line_decoded:
                    results['lines'] = lines
                    return
        except:
            break
        time.sleep(0.5)
    results['lines'] = lines


def call_mcp_understand_image(image_data_url: str, prompt: str = "请识别这张图片中的食材，以JSON数组格式返回，每个元素包含：name(食材名称)、type(类别：蔬菜/肉类/蛋类/水产/豆制品/调味品/主食/水果/其他)。只返回JSON数组，不要其他文字。") -> dict:
    """
    通过 MCP 调用 MiniMax 的 understand_image 工具
    """
    # 获取 MCP 服务器配置
    api_key = os.environ.get("MINIMAX_API_KEY", "sk-cp-U1_sSqUepX3JznIwxML8uzBPa-9ikjp_GMoXpZ63Lc_va7o7JPTjbqPGMIqh_B9WChJihqqxFtY-inPUfXlVBAOp0T23Q_2fpj3WiJeoJUV4GjXqfzzOQBo")
    api_host = os.environ.get("MINIMAX_API_HOST", "https://api.minimaxi.com")

    # 启动 MCP 服务器进程
    mcp_cmd = [
        "C:\\Users\\29640\\.local\\bin\\uvx.exe",
        "minimax-coding-plan-mcp",
        "-y"
    ]

    env = os.environ.copy()
    env["MINIMAX_API_KEY"] = api_key
    env["MINIMAX_API_HOST"] = api_host

    try:
        # 启动进程
        process = subprocess.Popen(
            mcp_cmd,
            stdin=subprocess.PIPE,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            env=env,
            text=False  # binary mode
        )

        # 等待进程启动
        time.sleep(3)

        # 1. 发送 initialize 请求
        init_request = {
            "jsonrpc": "2.0",
            "id": 0,
            "method": "initialize",
            "params": {
                "protocolVersion": "2024-11-05",
                "capabilities": {},
                "clientInfo": {
                    "name": "mcp-image-client",
                    "version": "1.0.0"
                }
            }
        }
        process.stdin.write((json.dumps(init_request) + "\n").encode('utf-8'))
        process.stdin.flush()

        # 读取初始化响应
        init_response = process.stdout.readline()
        if init_response:
            print(f"Init response: {init_response.decode('utf-8', errors='ignore')}", file=sys.stderr)

        # 2. 发送 initialized 通知
        initialized_notification = {
            "jsonrpc": "2.0",
            "method": "initialized",
            "params": {}
        }
        process.stdin.write((json.dumps(initialized_notification) + "\n").encode('utf-8'))
        process.stdin.flush()

        # 3. 发送工具调用请求
        tool_request = {
            "jsonrpc": "2.0",
            "id": 1,
            "method": "tools/call",
            "params": {
                "name": "understand_image",
                "arguments": {
                    "prompt": prompt,
                    "image_url": image_data_url
                }
            }
        }
        process.stdin.write((json.dumps(tool_request) + "\n").encode('utf-8'))
        process.stdin.flush()

        # 使用线程读取响应，给予更长时间
        results = {}
        reader_thread = threading.Thread(target=read_output, args=(process, results, 120))
        reader_thread.start()
        reader_thread.join(timeout=120)

        if 'lines' in results:
            for line in results['lines']:
                try:
                    response = json.loads(line)
                    if "result" in response:
                        result = response["result"]
                        if "content" in result:
                            content = result["content"]
                            if isinstance(content, list) and len(content) > 0:
                                text = content[0].get("text", "")
                                return {"success": True, "data": text}
                    if "error" in response:
                        return {"success": False, "error": response["error"]}
                except json.JSONDecodeError:
                    continue

        return {"success": False, "error": "No valid response from MCP"}

    except Exception as e:
        return {"success": False, "error": str(e)}
    finally:
        try:
            process.stdin.close()
            process.stdout.close()
            process.stderr.close()
        except:
            pass
        try:
            process.kill()
        except:
            pass


def main():
    if len(sys.argv) < 2:
        print(json.dumps({"error": "Usage: mcp_image_client.py <image_data_url> [prompt]"}))
        sys.exit(1)

    image_data_url = sys.argv[1]

    # 如果有第三个参数，作为 prompt
    prompt = sys.argv[2] if len(sys.argv) > 2 else None

    result = call_mcp_understand_image(image_data_url, prompt)
    print(json.dumps(result, ensure_ascii=False))


if __name__ == "__main__":
    main()
