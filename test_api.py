#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import urllib.request
import json
import sys

data = json.dumps({
    'type': 'image',
    'text': '',
    'image': 'https://img.freepik.com/free-photo/raw-meat_1339-857.jpg?w=300'
}).encode('utf-8')

req = urllib.request.Request(
    'http://localhost:8080/api/food/recognize',
    data=data,
    headers={'Content-Type': 'application/json'}
)

try:
    with urllib.request.urlopen(req, timeout=60) as response:
        result = response.read().decode('utf-8')
        print(result)
except Exception as e:
    print(f'Error: {e}', file=sys.stderr)
    import traceback
    traceback.print_exc()