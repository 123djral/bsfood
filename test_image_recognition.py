#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import urllib.request
import json
import sys

url = "http://localhost:8080/api/food/recognize"
data = {
    "type": "image",
    "text": "",
    "image": "https://img.freepik.com/free-photo/raw-meat_1339-857.jpg?w=300"
}

req = urllib.request.Request(
    url,
    data=json.dumps(data).encode('utf-8'),
    headers={'Content-Type': 'application/json'}
)

try:
    with urllib.request.urlopen(req, timeout=90) as response:
        result = response.read().decode('utf-8')
        # Write to file instead of printing
        with open('C:/temp/api_result.txt', 'w', encoding='utf-8') as f:
            f.write(result)
except Exception as e:
    with open('C:/temp/api_result.txt', 'w', encoding='utf-8') as f:
        f.write(f"Error: {str(e)}")