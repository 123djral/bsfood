#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import urllib.request
import json

data = json.dumps({
    'model': 'qwen3-vl-plus',
    'messages': [
        {
            'role': 'user',
            'content': [
                {'type': 'image_url', 'image_url': {'url': 'https://img.freepik.com/free-photo/raw-meat_1339-857.jpg?w=300'}},
                {'type': 'text', 'text': '请识别这张图片中的食材，以JSON数组格式返回，每个元素包含：name(食材名称)、type(类别：蔬菜/肉类/蛋类/水产/豆制品/调味品/主食/水果/其他)。只返回JSON数组，不要其他文字。'}
            ]
        }
    ]
}, ensure_ascii=False).encode('utf-8')

req = urllib.request.Request(
    'https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions',
    data=data,
    headers={
        'Content-Type': 'application/json',
        'Authorization': 'Bearer sk-80bf39560cf34c10a78f8e6c90de829b'
    }
)

try:
    with urllib.request.urlopen(req, timeout=60) as response:
        result = response.read().decode('utf-8')
        resp_json = json.loads(result)
        content = resp_json['choices'][0]['message']['content']
        print(f"Content type: {type(content)}")
        print(f"Content: {content}")
        # Parse the content as JSON
        foods = json.loads(content)
        print(f"Parsed foods: {foods}")
except Exception as e:
    print(f'Error: {e}')
    import traceback
    traceback.print_exc()