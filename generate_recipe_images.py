#!/usr/bin/env python3
"""
使用 MiniMax image-01 API 批量生成食谱图片
"""
import os
import requests
import base64
import time
import sys

# 配置
API_KEY = "sk-cp-U1_sSqUepX3JznIwxML8uzBPa-9ikjp_GMoXpZ63Lc_va7o7JPTjbqPGMIqh_B9WChJihqqxFtY-inPUfXlVBAOp0T23Q_2fpj3WiJeoJUV4GjXqfzzOQBo"
API_URL = "https://api.minimaxi.com/v1/image_generation"
PICTURE_DIR = "C:/develop/codeBase/bsfood1/picture"

# 菜名列表（从已有图片和WPS文档推断）
DISH_NAMES = [
    "红烧肉", "宫保鸡丁", "鱼香肉丝", "麻婆豆腐", "回锅肉", "糖醋里脊", "红烧排骨", "红烧鱼",
    "水煮鱼", "水煮肉片", "干锅花菜", "干煸四季豆", "干锅香辣土豆片", "地三鲜", "农家小炒肉",
    "京酱肉丝", "木须肉", "蒜苔炒肉", "芹菜炒肉", "土豆烧牛肉", "萝卜炖牛腩", "可乐鸡翅",
    "啤酒鸭", "葱爆羊肉", "孜然羊肉", "清蒸鱼", "冬瓜排骨汤", "丝瓜蛋汤", "番茄蛋汤", "紫菜蛋花汤",
    "凉拌黄瓜", "凉拌木耳", "凉拌藕片", "皮蛋豆腐", "清炒时蔬", "清炒土豆丝配尖椒番茄片",
    "番茄炒蛋", "番茄荷兰豆辣椒炒蛋", "番茄炒蛋（辣味版）", "奶香甜蛋卷配焦糖酱", "椰奶焦糖布丁",
    "燕麦酥烤三文鱼块", "柠檬蜜烤三文鱼配燕麦", "甜蜜坚果三文鱼沙拉碗", "泰式香辣牛肉番茄生菜卷",
    "茼蒿牛肉滑蛋", "小米辣香菜拌牛肉", "香煎三文鱼配红酒黑椒牛肉", "烤箱烤蔬菜配牛排",
    "焦糖蛋奶烤碗", "肉桂甜蛋奶燕麦粥", "料碗"
]

# 英文名映射
ENGLISH_NAMES = {
    "红烧肉": "braised pork",
    "宫保鸡丁": "kung pao chicken",
    "鱼香肉丝": "yu xiang shredded pork",
    "麻婆豆腐": "mapo tofu",
    "回锅肉": "twice-cooked pork",
    "糖醋里脊": "sweet and sour pork ribs",
    "红烧排骨": "braised pork ribs",
    "红烧鱼": "braised fish",
    "水煮鱼": "boiled fish",
    "水煮肉片": "boiled pork slices",
    "干锅花菜": "dry wok cauliflower",
    "干煸四季豆": "dry fried green beans",
    "干锅香辣土豆片": "dry wok spicy potato slices",
    "地三鲜": "earth three fresh",
    "农家小炒肉": "rural stir-fried pork",
    "京酱肉丝": "Beijing style pork",
    "木须肉": "moo shu pork",
    "蒜苔炒肉": "garlic scape stir-fried pork",
    "芹菜炒肉": "celery stir-fried pork",
    "土豆烧牛肉": "potato braised beef",
    "萝卜炖牛腩": "radish braised beef brisket",
    "可乐鸡翅": "cola chicken wings",
    "啤酒鸭": "beer duck",
    "葱爆羊肉": "scallion stir-fried lamb",
    "孜然羊肉": "cumin lamb",
    "清蒸鱼": "steamed fish",
    "冬瓜排骨汤": "winter melon pork rib soup",
    "丝瓜蛋汤": "sponge gourd egg soup",
    "番茄蛋汤": "tomato egg soup",
    "紫菜蛋花汤": "seaweed egg drop soup",
    "凉拌黄瓜": "cold cucumber",
    "凉拌木耳": "cold wood ear mushroom",
    "凉拌藕片": "cold lotus root slices",
    "皮蛋豆腐": "preserved egg tofu",
    "清炒时蔬": "stir-fried vegetables",
    "清炒土豆丝配尖椒番茄片": "stir-fried potato with peppers",
    "番茄炒蛋": "tomato scrambled eggs",
    "番茄荷兰豆辣椒炒蛋": "tomato pea pepper eggs",
    "番茄炒蛋（辣味版）": "spicy tomato eggs",
    "奶香甜蛋卷配焦糖酱": "milk sweet egg roll with caramel",
    "椰奶焦糖布丁": "coconut milk caramel pudding",
    "燕麦酥烤三文鱼块": "oat roasted salmon",
    "柠檬蜜烤三文鱼配燕麦": "lemon honey salmon with oats",
    "甜蜜坚果三文鱼沙拉碗": "sweet nut salmon salad bowl",
    "泰式香辣牛肉番茄生菜卷": "Thai spicy beef lettuce wrap",
    "茼蒿牛肉滑蛋": "crown Daisy beef egg",
    "小米辣香菜拌牛肉": "millet pepper cilantro beef",
    "香煎三文鱼配红酒黑椒牛肉": "pan seared salmon with red wine beef",
    "烤箱烤蔬菜配牛排": "oven roasted vegetables with steak",
    "焦糖蛋奶烤碗": "caramel egg milk baked bowl",
    "肉桂甜蛋奶燕麦粥": "cinnamon sweet egg milk oatmeal",
    "料碗": "seasoning bowl"
}

def generate_image(recipe_name, english_name=None):
    if english_name is None:
        english_name = ENGLISH_NAMES.get(recipe_name, recipe_name)

    prompt = f"A professional food photography of {english_name}, Chinese dish, appetizing presentation, restaurant style, top-down angle, natural lighting, 4K quality"

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
    # 删除已有图片
    print("删除已有图片...")
    for f in os.listdir(PICTURE_DIR):
        if f.endswith('.jpg') or f.endswith('.png'):
            try:
                os.remove(os.path.join(PICTURE_DIR, f))
            except:
                pass

    print(f"开始为 {len(DISH_NAMES)} 个菜名生成图片...\n")

    success = 0
    fail = 0

    for i, name in enumerate(DISH_NAMES, 1):
        print(f"[{i}/{len(DISH_NAMES)}] 正在生成: {name}")

        english_name = ENGLISH_NAMES.get(name, name)
        image_url = generate_image(name, english_name)

        if image_url:
            safe_name = name.replace('/', '_').replace('\\', '_').replace('*', '_').replace('?', '_')
            filepath = download_image(image_url, f"{safe_name}.jpg")
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
