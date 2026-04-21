#!/usr/bin/env python3
"""批量生成食谱图片"""

import os
import requests
import time

# MiniMax API配置
API_KEY = "sk-cp-U1_sSqUepX3JznIwxML8uzBPa-9ikjp_GMoXpZ63Lc_va7o7JPTjbqPGMIqh_B9WChJihqqxFtY-inPUfXlVBAOp0T23Q_2fpj3WiJeoJUV4GjXqfzzOQBo"
API_URL = "https://api.minimaxi.com/v1/image_generation"

# 本地保存路径
PICTURE_PATH = "C:/develop/codeBase/bsfood1/picture"

# 50道菜品列表
DISHES = [
    "鱼香肉丝", "宫保鸡丁", "麻婆豆腐", "水煮肉片", "回锅肉",
    "糖醋里脊", "青椒土豆丝", "番茄炒蛋", "酸辣土豆丝", "红烧排骨",
    "红烧肉", "可乐鸡翅", "清炒时蔬", "蒜蓉油麦菜", "香菇青菜",
    "干煸四季豆", "地三鲜", "韭菜炒鸡蛋", "芹菜炒肉", "蒜苔炒肉",
    "红烧鱼", "清蒸鱼", "酸菜鱼", "水煮鱼", "辣子鸡",
    "孜然羊肉", "葱爆羊肉", "京酱肉丝", "木须肉", "锅包肉",
    "农家小炒肉", "手撕包菜", "酸辣白菜", "冬瓜排骨汤", "番茄蛋汤",
    "紫菜蛋花汤", "丝瓜蛋汤", "香菇滑鸡", "土豆烧牛肉", "萝卜炖牛腩",
    "黄焖鸡", "啤酒鸭", "卤猪蹄", "酱牛肉", "凉拌黄瓜",
    "凉拌木耳", "凉拌藕片", "皮蛋豆腐", "麻酱豆角", "干锅花菜"
]

def generate_image(dish_name):
    """使用MiniMax image-01生成图片"""
    prompt = f"专业美食摄影，中式家常菜《{dish_name}》，正面拍摄，背景简洁，食欲感强，8K高清，真实风格"

    headers = {
        "Authorization": f"Bearer {API_KEY}",
        "Content-Type": "application/json"
    }

    data = {
        "model": "image-01",
        "prompt": prompt,
        "image_size": "1:1",
        "number": 1
    }

    try:
        response = requests.post(API_URL, headers=headers, json=data, timeout=180)
        result = response.json()

        if response.status_code == 200 and result.get("data"):
            image_urls = result["data"].get("image_urls")
            if image_urls and len(image_urls) > 0:
                image_url = image_urls[0]
            return image_url
        else:
            print(f"  API错误: {result}")
            return None
    except Exception as e:
        print(f"  请求异常: {e}")
        return None

def download_image(url, file_path):
    """下载图片到本地"""
    try:
        response = requests.get(url, timeout=180, headers={"User-Agent": "Mozilla/5.0"})
        if response.status_code == 200:
            with open(file_path, 'wb') as f:
                f.write(response.content)
            return len(response.content)
        return 0
    except Exception as e:
        print(f"  下载异常: {e}")
        return 0

def clean_filename(name):
    """清理文件名"""
    return name.replace("/", "_").replace("\\", "_").replace(":", "_").replace("*", "_").replace("?", "_").replace('"', "_").replace("<", "_").replace(">", "_").replace("|", "_").replace(" ", "_")

def main():
    print(f"开始生成 {len(DISHES)} 道菜品的图片...")
    print(f"保存路径: {PICTURE_PATH}")
    print("-" * 50)

    os.makedirs(PICTURE_PATH, exist_ok=True)

    success_count = 0
    skip_count = 0

    for i, dish in enumerate(DISHES, 1):
        file_name = clean_filename(dish) + ".jpg"
        file_path = os.path.join(PICTURE_PATH, file_name)

        # 检查是否已存在
        if os.path.exists(file_path):
            print(f"[{i}/{len(DISHES)}] 跳过(已存在): {dish}")
            skip_count += 1
            continue

        print(f"[{i}/{len(DISHES)}] 生成中: {dish} ...", end=" ", flush=True)

        # 生成图片
        image_url = generate_image(dish)

        if image_url:
            # 下载保存
            size = download_image(image_url, file_path)
            if size > 0:
                print(f"成功 ({size} bytes)")
                success_count += 1
            else:
                print("失败(下载)")
        else:
            print("失败(生成)")

        # 避免API限流
        time.sleep(1)

    print("-" * 50)
    print(f"完成! 成功: {success_count}, 跳过: {skip_count}, 总计: {len(DISHES)}")

if __name__ == "__main__":
    main()
