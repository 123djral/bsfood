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
        "name": "腐竹烧牛腩",
        "prompt": "腐竹烧牛腩，写实美食摄影，软糯腐竹炖牛腩，汤汁浓郁，暖光，陶瓷大碗，热气腾腾，8K 高清，中式家常菜"
    },
    {
        "name": "干锅肥肠",
        "prompt": "干锅肥肠，香辣干锅肥肠，焦香入味，洋葱青椒打底，铁锅装盘，川菜，美食特写摄影，8K 高清"
    },
    {
        "name": "滑炒里脊片",
        "prompt": "滑炒里脊片，鲜嫩滑炒里脊片，配木耳黄瓜，清淡鲜香，白瓷盘，家常菜，自然光美食摄影，8K"
    },
    {
        "name": "红烧鲳鱼",
        "prompt": "红烧鲳鱼，色泽红亮，鱼肉细嫩，葱花点缀，中式家常菜，高清美食摄影，8K 写实"
    },
    {
        "name": "蒜蓉粉丝蒸虾",
        "prompt": "蒜蓉粉丝蒸虾，整齐摆盘，鲜香入味，蒸汽袅袅，海鲜美食，8K 写实摄影"
    },
    {
        "name": "韭菜炒鸡蛋",
        "prompt": "家常韭菜炒鸡蛋，金黄翠绿，简单下饭，白盘，治愈系美食摄影，8K 高清"
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
