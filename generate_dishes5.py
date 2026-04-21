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

# 菜品列表
DISHES = [
    {
        "name": "爆炒鱿鱼须",
        "prompt": "香辣爆炒鱿鱼须，锅气十足，青椒洋葱，铁板风格，美食摄影，8K 高清"
    },
    {
        "name": "萝卜丝炖虾",
        "prompt": "萝卜丝炖鲜虾，汤色奶白，鲜香味美，汤碗，养生家常菜，高清写实，8K"
    },
    {
        "name": "凉拌藕片",
        "prompt": "酸甜凉拌藕片，清脆爽口，芝麻香菜，白盘，开胃凉菜，自然光摄影，8K"
    },
    {
        "name": "凉拌鸡丝",
        "prompt": "凉拌鸡丝，黄瓜丝胡萝卜丝，红油酱汁，凉菜，美食特写摄影，8K 高清"
    },
    {
        "name": "炸酥肉",
        "prompt": "金黄酥脆炸酥肉，外酥里嫩，撒辣椒面，木盘，小吃美食摄影，8K 高清"
    },
    {
        "name": "蒸水蛋",
        "prompt": "嫩滑蒸水蛋，淋生抽香油，葱花，瓷碗，家常蒸菜，治愈系高清摄影，8K"
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
