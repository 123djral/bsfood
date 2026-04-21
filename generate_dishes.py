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
        "name": "梅菜扣肉",
        "prompt": "梅菜扣肉写实美食摄影，肥而不腻梅菜扣肉，色泽红亮，扣碗倒扣摆盘，热气腾腾，中式家常菜，8K 高清"
    },
    {
        "name": "剁椒鱼头",
        "prompt": "超大份剁椒鱼头，红亮剁椒铺满，鲜辣入味，白瓷大盘，蒸汽袅袅，湘菜特写，8K 高清"
    },
    {
        "name": "毛血旺",
        "prompt": "川味毛血旺，鸭血毛肚黄喉，红油麻辣，大盆装，热气，美食特写，质感细腻，8K 高清"
    },
    {
        "name": "香锅牛蛙",
        "prompt": "香辣干锅牛蛙，外酥里嫩，芝麻香菜点缀，铁锅，暖光，川菜美食摄影，8K 高清"
    },
    {
        "name": "红烧狮子头",
        "prompt": "红烧大狮子头，酱汁浓郁，青菜围边，白瓷盘，中式家常菜，高清写实，8K 高清"
    },
    {
        "name": "板栗烧鸡",
        "prompt": "板栗烧鸡，鸡肉软烂板栗香甜，砂锅，暖光，家常美食，自然光特写，8K 高清"
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
