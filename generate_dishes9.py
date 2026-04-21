#!/usr/bin/env python3
"""
使用 MiniMax image-01 API 生成特定菜品图片
"""
import os
import requests
import time

# 配置
API_KEY = "sk-cp-U1_sSqUepX3JznIwxML8uzBPa-9ikjp_GMoXpZ63Lc_va7o7JPTjbqPGMIqh_B9WChJihqqxFtY-inPUfXlVBAOp0T23Q_2fpj3WiJeoJUV4GjXqfzzOQBo"
API_URL = "https://api.minimaxi.com/v1/image_generation"
PICTURE_DIR = "C:/develop/codeBase/bsfood1/picture"

# 菜品列表（根据用户描述）
DISHES = [
    {
        "name": "香菇炒青菜",
        "prompt": "香菇炒上海青，油亮翠绿，清淡素菜，美食特写，自然光摄影，8K 高清"
    },
    {
        "name": "酸辣藕丁",
        "prompt": "酸辣脆爽藕丁，红椒葱花，开胃凉菜，白盘，高清细节特写，8K"
    },
    {
        "name": "香干炒芹菜",
        "prompt": "香干炒芹菜，清爽解腻，家常小炒，暖光，美食摄影，8K 高清"
    },
    {
        "name": "红烧鸭掌",
        "prompt": "红烧鸭掌，软糯入味，酱香浓郁，瓷盘，中式下酒菜特写，8K 高清"
    },
    {
        "name": "清炒藕片",
        "prompt": "清炒藕片，洁白脆嫩，简单素菜，自然光，高清美食摄影，8K"
    },
    {
        "name": "木耳炒山药",
        "prompt": "木耳炒山药，清淡爽口，健康素菜，白盘，美食摄影，8K 高清"
    }
]

def generate_image(prompt):
    payload = {
        "model": "image-01",
        "prompt": prompt,
        "aspect_ratio": "1:1",
        "response_format": "url",
        "n": 1,
        "prompt_optimizer": True
    }

    headers = {
        "Authorization": f"Bearer {API_KEY}",
        "Content-Type": "application/json"
    }

    try:
        response = requests.post(API_URL, json=payload, headers=headers, timeout=120)
        result = response.json()

        if response.status_code == 200 and result.get("base_resp", {}).get("status_code") == 0:
            image_url = result["data"]["image_urls"][0]
            return image_url
        else:
            print(f"  生成失败: {result.get('base_resp', {}).get('status_msg', '未知错误')}")
            return None
    except Exception as e:
        print(f"  请求异常: {e}")
        return None

def download_image(url, filename):
    try:
        response = requests.get(url, timeout=60)
        if response.status_code == 200:
            filepath = os.path.join(PICTURE_DIR, filename)
            with open(filepath, 'wb') as f:
                f.write(response.content)
            return filepath
        return None
    except Exception as e:
        print(f"  下载异常: {e}")
        return None

def main():
    print(f"开始为 {len(DISHES)} 个菜品生成图片...\n")

    success = 0
    fail = 0

    for i, dish in enumerate(DISHES, 1):
        name = dish["name"]
        prompt = dish["prompt"]

        print(f"[{i}/{len(DISHES)}] 正在生成: {name}")

        image_url = generate_image(prompt)

        if image_url:
            filepath = download_image(image_url, f"{name}.jpg")
            if filepath:
                print(f"  成功: {filepath}")
                success += 1
            else:
                fail += 1
        else:
            fail += 1

        # 避免请求过快
        time.sleep(3)

    print(f"\n完成！成功: {success}, 失败: {fail}")

if __name__ == "__main__":
    main()
