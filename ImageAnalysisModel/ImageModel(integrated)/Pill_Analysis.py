'''
Copyright (C) <2022> <Kang Juhyeong, Lee Sumin, Son Youngju>
 
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.
 
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.
 
You should have received a copy of the GNU General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>
'''

from glob import glob
from PIL import Image
from rembg.bg import remove
from IPython.display import Image
from sklearn.cluster import KMeans
from collections import OrderedDict
import cv2
import os
import easyocr
import easydict
import argparse
import numpy as np
import matplotlib.pyplot as plt
import sys
import string

result = []
H = []
S = []
V = []
Sum = []
tmp = []

def yoloExe(img):
    # %cd /content/yolov5/
    img_list = glob(img)

    val_img_path = img_list[0]

    # weights_path = '/content/pill_yolo.pt'
    weights_path = 'C:/Users/lukai/Desktop/pill_yolo.pt'

    os.system('python C:/users/lukai/desktop/yolov5/detect.py --weights ' + weights_path + ' --img 416 --conf 0.5 --source ' + val_img_path + ' --line-thickness 1 --save-crop')

    detect_img_path = img
    Image(os.path.join(detect_img_path, os.path.basename(val_img_path)))

def setLabel(img, pts, label): #비율 계산에 사용되는 레이블 함수
    (x, y, w, h) = cv2.boundingRect(pts)
    pt1 = (x, y)
    pt2 = (x + w, y + h)
    cv2.rectangle(img, pt1, pt2, (0, 255, 0), 2)
    cv2.putText(img, label, (pt1[0], pt1[1]-3), cv2.FONT_HERSHEY_SIMPLEX, 0.7, (0, 0, 255))

def rmBg(path):
    input_path = path
    output_path = sys.argv[1] + '\\\\rmbg.jpg'

    with open(input_path, 'rb') as i:
        with open(output_path, 'wb') as o:
            input = i.read()
            output = remove(input)
            o.write(output)
    return sys.argv[1] + '\\\\rmbg.jpg'

def binary(path):
    img = cv2.imread(path)
    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    ret, dst = cv2.threshold (gray, 2, 255, cv2.THRESH_BINARY)
    cv2.imwrite(sys.argv[1] + '\\\\binary.jpg', dst)

    img = cv2.imread(sys.argv[1] + '\\\\binary.jpg')
    k = cv2.getStructuringElement(cv2.MORPH_RECT, (3,3))
    # 팽창 연산 적용
    dilate = cv2.dilate(img, k)
    cv2.imwrite(sys.argv[1] + '\\\\dilate.jpg', dilate)

    img2 = cv2.imread(sys.argv[1] + '\\\\dilate.jpg')
    dilate2 = cv2.dilate(img2, k)
    cv2.imwrite(sys.argv[1] + '\\\\dilate2.jpg', dilate2)

    return sys.argv[1] + '\\\\dilate2.jpg'

def reverse(path): #이진화된 이미지 반전시키는 함수
    img = cv2.imread(path)
    out = img.copy()
    out = 255 - out
    cv2.imwrite(sys.argv[1] + '\\\\pill.jpg', out)
    return sys.argv[1] + '\\\\pill.jpg'

def cont(path): #원, 타원, 장방형의 각형을 알려주는 함수
    img = cv2.imread(path)
    img2 = img.copy()
    img_gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    res, thr = cv2.threshold(img_gray, 127, 255, cv2.THRESH_BINARY)
    contours, hierarchy = cv2.findContours(thr, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)

    cnt = contours[0]
    cv2.drawContours(img, [cnt], -1, (255, 255, 0), 2)

    epsilon2 =  0.005 * cv2.arcLength(cnt, True)
    approx2 = cv2.approxPolyDP(cnt, epsilon2, True)
    cv2.drawContours(img2, [approx2], -1, (0, 255, 0), 3)

    print("[Case1.제형] n각형 분석 : "+str(len(approx2))+"각형")
    #detect(path)
    #print(len(approx2))
    if len(approx2) == 4:
        detect_4(path)
    elif len(approx2) == 5 or len(approx2) == 6 or len(approx2) == 7:
        detect_5to7(path)
    elif len(approx2) == 8 or len(approx2) == 9 or len(approx2) == 10:
        detect_8to10(path)
    elif len(approx2) == 11 or len(approx2) == 12:
        detect_11to12(path)
    elif len(approx2) == 13 or len(approx2) == 14:
        detect_13to14(path)
    elif len(approx2) == 15 or len(approx2) == 16:
        detect_15to16(path)
    elif len(approx2) >= 17:
        detect_17to21(path)

def detect_4(path):
    src = cv2.imread(reverse(path))
    dst = src.copy()

    gray = cv2.cvtColor(src, cv2.COLOR_RGB2GRAY)
    ret, binary = cv2.threshold(gray, 10, 255, cv2.THRESH_BINARY_INV)

    contours, hierarchy = cv2.findContours(binary, cv2.RETR_CCOMP, cv2.CHAIN_APPROX_NONE)
    for i in contours:
        M = cv2.moments(i)
        cX = int(M['m10'] / M['m00'])
        cY = int(M['m01'] / M['m00'])
        
        cv2.circle(dst, (cX, cY), 3, (0, 0, 255), -1)
        cv2.drawContours(dst, [i], 0, (0, 0, 255), 2)

        setLabel(dst, i, '')
        (x, y, w, h) = cv2.boundingRect(i)
    if w > h:
      ratio = h / w
    elif h > w:
      ratio = w / h
    elif h == w:
      ratio = h / w

    if ratio > 0.1 and ratio <= 0.45:
        result.append("장방형")
        print("[Case2.비율] 비율 분석(장축/단축 비율) : "+str(ratio))
    elif ratio > 0.45 and ratio <= 0.90:
        result.append("타원형")
        print("[Case2.비율] 비율 분석(장축/단축 비율) : "+str(ratio))
    elif ratio > 0.9 and ratio <= 0.95:
        result.append("사각형")
        print("[Case2.비율] 비율 분석(장축/단축 비율) : "+str(ratio))
    elif ratio > 0.95 and ratio <= 1.0:
        result.append("원형")
        print("[Case2.비율] 비율 분석(장축/단축 비율) : "+str(ratio))

def detect_5to7(path):
    src = cv2.imread(reverse(path))
    dst = src.copy()

    gray = cv2.cvtColor(src, cv2.COLOR_RGB2GRAY)
    ret, binary = cv2.threshold(gray, 10, 255, cv2.THRESH_BINARY_INV)

    contours, hierarchy = cv2.findContours(binary, cv2.RETR_CCOMP, cv2.CHAIN_APPROX_NONE)
    for i in contours:
        M = cv2.moments(i)
        cX = int(M['m10'] / M['m00'])
        cY = int(M['m01'] / M['m00'])
        
        cv2.circle(dst, (cX, cY), 3, (0, 0, 255), -1)
        cv2.drawContours(dst, [i], 0, (0, 0, 255), 2)

        setLabel(dst, i, '')
        (x, y, w, h) = cv2.boundingRect(i)
    if w > h:
      ratio = h / w
    elif h > w:
      ratio = w / h
    elif h == w:
      ratio = h / w

    if ratio > 0.1 and ratio <= 0.45:
        result.append("장방형")
        print("[Case2.비율] 비율 분석(장축/단축 비율) : "+str(ratio))
    elif ratio > 0.45 and ratio <= 1.0:
        result.append("타원형")
        print("[Case2.비율] 비율 분석(장축/단축 비율) : "+str(ratio))

def detect_8to10(path):
    src = cv2.imread(reverse(path))
    dst = src.copy()

    gray = cv2.cvtColor(src, cv2.COLOR_RGB2GRAY)
    ret, binary = cv2.threshold(gray, 10, 255, cv2.THRESH_BINARY_INV)

    contours, hierarchy = cv2.findContours(binary, cv2.RETR_CCOMP, cv2.CHAIN_APPROX_NONE)
    for i in contours:
        M = cv2.moments(i)
        cX = int(M['m10'] / M['m00'])
        cY = int(M['m01'] / M['m00'])
        
        cv2.circle(dst, (cX, cY), 3, (0, 0, 255), -1)
        cv2.drawContours(dst, [i], 0, (0, 0, 255), 2)

        setLabel(dst, i, '')
        (x, y, w, h) = cv2.boundingRect(i)
    if w > h:
      ratio = h / w
    elif h > w:
      ratio = w / h
    elif h == w:
      ratio = h / w

    if ratio > 0.1 and ratio <= 0.65:
        result.append("장방형")
        print("[Case2.비율] 비율 분석(장축/단축 비율) : "+str(ratio))
    elif ratio > 0.65 and ratio <= 1.0:
        result.append("팔각형")
        print("[Case2.비율] 비율 분석(장축/단축 비율) : "+str(ratio))

def detect_11to12(path):
    src = cv2.imread(reverse(path))
    dst = src.copy()

    gray = cv2.cvtColor(src, cv2.COLOR_RGB2GRAY)
    ret, binary = cv2.threshold(gray, 10, 255, cv2.THRESH_BINARY_INV)

    contours, hierarchy = cv2.findContours(binary, cv2.RETR_CCOMP, cv2.CHAIN_APPROX_NONE)
    for i in contours:
        M = cv2.moments(i)
        cX = int(M['m10'] / M['m00'])
        cY = int(M['m01'] / M['m00'])
        
        cv2.circle(dst, (cX, cY), 3, (0, 0, 255), -1)
        cv2.drawContours(dst, [i], 0, (0, 0, 255), 2)

        setLabel(dst, i, '')
        (x, y, w, h) = cv2.boundingRect(i)
    if w > h:
      ratio = h / w
    elif h > w:
      ratio = w / h
    elif h == w:
      ratio = h / w

    if ratio > 0.1 and ratio <= 0.45:
        result.append("장방형")
        print("[Case2.비율] 비율 분석(장축/단축 비율) : "+str(ratio))
    elif ratio > 0.9 and ratio <= 1.0:
        result.append("사각형")
        print("[Case2.비율] 비율 분석(장축/단축 비율) : "+str(ratio))

def detect_13to14(path):
    src = cv2.imread(reverse(path))
    dst = src.copy()

    gray = cv2.cvtColor(src, cv2.COLOR_RGB2GRAY)
    ret, binary = cv2.threshold(gray, 10, 255, cv2.THRESH_BINARY_INV)

    contours, hierarchy = cv2.findContours(binary, cv2.RETR_CCOMP, cv2.CHAIN_APPROX_NONE)
    for i in contours:
        M = cv2.moments(i)
        cX = int(M['m10'] / M['m00'])
        cY = int(M['m01'] / M['m00'])
        
        cv2.circle(dst, (cX, cY), 3, (0, 0, 255), -1)
        cv2.drawContours(dst, [i], 0, (0, 0, 255), 2)

        setLabel(dst, i, '')
        (x, y, w, h) = cv2.boundingRect(i)
    if w > h:
      ratio = h / w
    elif h > w:
      ratio = w / h
    elif h == w:
      ratio = h / w

    if ratio > 0.1 and ratio <= 0.45:
        result.append("장방형")
        print("[Case2.비율] 비율 분석(장축/단축 비율) : "+str(ratio))
    elif ratio > 0.45 and ratio <= 0.9:
        result.append("타원형")
        print("[Case2.비율] 비율 분석(장축/단축 비율) : "+str(ratio))
    elif ratio > 0.9 and ratio <= 1.0:
        result.append("사각형")
        print("[Case2.비율] 비율 분석(장축/단축 비율) : "+str(ratio))

def detect_15to16(path):
    src = cv2.imread(reverse(path))
    dst = src.copy()

    gray = cv2.cvtColor(src, cv2.COLOR_RGB2GRAY)
    ret, binary = cv2.threshold(gray, 10, 255, cv2.THRESH_BINARY_INV)

    contours, hierarchy = cv2.findContours(binary, cv2.RETR_CCOMP, cv2.CHAIN_APPROX_NONE)
    for i in contours:
        M = cv2.moments(i)
        cX = int(M['m10'] / M['m00'])
        cY = int(M['m01'] / M['m00'])
        
        cv2.circle(dst, (cX, cY), 3, (0, 0, 255), -1)
        cv2.drawContours(dst, [i], 0, (0, 0, 255), 2)

        setLabel(dst, i, '')
        (x, y, w, h) = cv2.boundingRect(i)
    if w > h:
      ratio = h / w
    elif h > w:
      ratio = w / h
    elif h == w:
      ratio = h / w

    if ratio > 0.1 and ratio <= 0.45:
        result.append("장방형")
        print("[Case2.비율] 비율 분석(장축/단축 비율) : "+str(ratio))
    elif ratio > 0.45 and ratio <= 0.9:
        result.append("타원형")
        print("[Case2.비율] 비율 분석(장축/단축 비율) : "+str(ratio))
    elif ratio > 0.9 and ratio <= 1.0:
        result.append("원형")
        print("[Case2.비율] 비율 분석(장축/단축 비율) : "+str(ratio))

def detect_17to21(path):
    src = cv2.imread(reverse(path))
    dst = src.copy()

    gray = cv2.cvtColor(src, cv2.COLOR_RGB2GRAY)
    ret, binary = cv2.threshold(gray, 10, 255, cv2.THRESH_BINARY_INV)

    contours, hierarchy = cv2.findContours(binary, cv2.RETR_CCOMP, cv2.CHAIN_APPROX_NONE)
    for i in contours:
        M = cv2.moments(i)
        cX = int(M['m10'] / M['m00'])
        cY = int(M['m01'] / M['m00'])
        
        cv2.circle(dst, (cX, cY), 3, (0, 0, 255), -1)
        cv2.drawContours(dst, [i], 0, (0, 0, 255), 2)

        setLabel(dst, i, '')
        (x, y, w, h) = cv2.boundingRect(i)
    if w > h:
      ratio = h / w
    elif h > w:
      ratio = w / h
    elif h == w:
      ratio = h / w

    if ratio > 0.45 and ratio <= 0.9:
        result.append("타원형")
        print("[Case2.비율] 비율 분석(장축/단축 비율) : "+str(ratio))
    elif ratio > 0.9 and ratio <= 1.0:
        result.append("원형")
        print("[Case2.비율] 비율 분석(장축/단축 비율) : "+str(ratio))

def centroid_histogram(clt):
	# grab the number of different clusters and create a histogram
	# based on the number of pixels assigned to each cluster
	numLabels = np.arange(0, len(np.unique(clt.labels_)) + 1)
	(hist, _) = np.histogram(clt.labels_, bins = numLabels)

	# normalize the histogram, such that it sums to one
	hist = hist.astype("float")
	hist /= hist.sum()

	# return the histogram
	return hist

def plot_colors(hist, centroids):
	# initialize the bar chart representing the relative frequency
	# of each of the colors
	bar = np.zeros((50, 300, 3), dtype = "uint8")
	startX = 0

	# loop over the percentage of each cluster and the color of
	# each cluster
	for (percent, color) in zip(hist, centroids):
		# plot the relative percentage of each cluster
		endX = startX + (percent * 300)
		cv2.rectangle(bar, (int(startX), 0), (int(endX), 50),
			color.astype("uint8").tolist(), -1)
		startX = endX
	
	# return the bar chart
	return bar

def first_largest_number(arr):
    unique_nums = set(arr)
    sorted_nums = sorted(unique_nums, reverse=True)
    return sorted_nums[0]  

def second_largest_number(arr):
    unique_nums = set(arr)
    sorted_nums = sorted(unique_nums, reverse=True)
    return sorted_nums[1]

'그레이 스케일'
def mtjin_bgr2gray(bgr_img):
    # BGR 색상값
    b = bgr_img[:, :, 0]
    g = bgr_img[:, :, 1]
    r = bgr_img[:, :, 2]
    result = ((0.299 * r) + (0.587 * g) + (0.114 * b))
    # imshow 는 CV_8UC3 이나 CV_8UC1 형식을 위한 함수이므로 타입변환
    return result.astype(np.uint8)

'히스토그램 평활화'
def histogram_equalization(img):
  src = cv2.imread(img, cv2.IMREAD_GRAYSCALE)
  dst = cv2.equalizeHist(src)
  #cv2_imshow(dst)
  cv2.imwrite(sys.argv[1] + '\\\\histogram_eq.jpg', dst)

'노이즈 제거(OCR)'
def rm_noise(img):
  src = cv2.imread(img, cv2.IMREAD_COLOR)
  denoised_img = cv2.fastNlMeansDenoisingColored(src, None, 15, 15, 5, 10)
  #cv2_imshow(src)
  #cv2_imshow(denoised_img)
  cv2.imwrite(sys.argv[1] + "\\\\denoise.jpg", denoised_img)

'노이즈 제거(제형)'
def rm_noise2(img):
  src = cv2.imread(img, cv2.IMREAD_COLOR)
  denoised_img = cv2.fastNlMeansDenoisingColored(src, None, 15, 15, 5, 10)
  #cv2_imshow(src)
  #cv2_imshow(denoised_img)
  cv2.imwrite(sys.argv[1] + "\\\\denoise2.jpg", denoised_img)

'OCR'
def ocr(img):
  reader = easyocr.Reader(['ko', 'en'])
  output = reader.readtext(img, detail = 0)
  joined_str = "".join(output) 
  correction_str = trim_space(joined_str)
  result.append(correction_str)
  print("[Case4.OCR] OCR 분석 결과 : "+correction_str )

def trim_space(input_str):
  trim_str = input_str.strip() #STEP1. strip() 메소드를 통해 문자열 앞, 뒤의 공백 제거
  new_str = trim_str.replace(" ", "") #STEP2. replace() 메소드를 통해 문자열 사이의 모든 공백 제거
  
  #STEP3. 특수문자 중, 실제 각인 및 프린팅에 사용되는 6종(+,-,/,|,.,&)은 제외 대상에서 제거한다.
  punctuation = string.punctuation 
  punctuation = punctuation.replace("+","")
  punctuation = punctuation.replace("-","")
  punctuation = punctuation.replace("/","")
  punctuation = punctuation.replace("|","")
  punctuation = punctuation.replace(".","")
  punctuation = punctuation.replace("&","")
  
  #STEP4. translate() 메소드를 통해 문자열에 존재하는 특수문자를 모두 제거한 문자열을 반환한다.
  output_str = new_str.translate(str.maketrans('', '', punctuation))
  return output_str

def main(): #메인함수
    #제형 구하는 부분
    yoloExe(sys.argv[1] + sys.argv[2]) #YOLO 함수 호출 #이미지 경로에 사용자가 촬영, 선택한 이미지 경로 넣어야함
    rm_path = rmBg("C:/Users/lukai/Desktop/yolov5/runs/detect/exp/crops/pill" + sys.argv[2]) #배경제거 함수 호출
    first_path = binary(rm_path)
    cont(first_path)

    #색상 구하는 부분
    args = easydict.EasyDict({
        "image": True,
        "clusters": 3
    })

    image = cv2.imread(rm_path) #이미지 경로에 rm_path 넣어야함
    image = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)

    #plt.figure()
    #plt.axis("off")
    #plt.imshow(image)

    image = image.reshape((image.shape[0] * image.shape[1], 3))

    clt = KMeans(n_clusters = args["clusters"])
    clt.fit(image)

    hist = centroid_histogram(clt)
    bar = plot_colors(hist, clt.cluster_centers_)

    #plt.figure()
    #plt.axis("off")
    #plt.imshow(bar)
    #plt.show()

    temp = clt.cluster_centers_
    color_value = temp.astype("uint8").tolist() #세 컬러의 rgb값
    second_num = second_largest_number(hist) #히스토그램에서 두 번째로 큰 값(두 번째 색)

    for i in range(3): #두 번째로 큰 값의 인덱스를 구함
      if (second_num == hist[i]):
        color_idx = i

    if((color_value[color_idx][0] < 20) and (color_value[color_idx][1]) < 20 and (color_value[color_idx][2] < 20)):
      first_num = first_largest_number(hist)
      for i in range(3): #첫 번째로 큰 값의 인덱스를 구함
        if (first_num == hist[i]):
          color_idx = i

    color_bgr = np.array([[[color_value[color_idx][2],color_value[color_idx][1],color_value[color_idx][0]]]], dtype=np.uint8)
    color_hsv = cv2.cvtColor(color_bgr, cv2.COLOR_BGR2HSV)
    h, s, v = cv2.split(color_hsv)

    H.append(h)
    S.append(s)
    V.append(v)

    Sum.append(h+s+v)
    tmp.append("(" + str(h) + "," + str(s) + "," + str(v) + ")")

    #[색상] STEP1. R+G+B, ABS(R-G)+ABS(R-B)+ABS(G-B)값을 기준으로 하여 검정, 하양 구분
    # color_sum(색상합) = R+G+B
    # color_diff(색상차) = ABS(R-G)+ABS(R-B)+ABS(G-B)
    color_sum = color_value[color_idx][0] + color_value[color_idx][1] + color_value[color_idx][2]
    color_diff = abs(color_value[color_idx][0]-color_value[color_idx][1]) + abs(color_value[color_idx][0]-color_value[color_idx][2]) + abs(color_value[color_idx][1]-color_value[color_idx][2])
    
    # 검정 및 하양의 검사 대상을 확보하기 위해, 색상차를 비롯한 채도(S) 및 명도(V)를 추가적으로 활용한다.
    if(color_diff<30 or s<30 or v<50):
      if((color_sum>=0 and color_sum<350)):
        result.append("검정")
      elif((color_sum>=350 and color_sum<766)):
        result.append("하양")

    #[색상] STEP2. RGB -> HSV 변환을 통해 H(색상값)를 기준으로 하여 빨강, 노랑, 초록, 파랑 구분
    # OpenCV의 HSV에 대한 H 최대값은 180이다.

    # STEP2-1. 빨강에 대한 구분(빨강, 주황, 갈색, 분홍)
    else:
      if((h>=0 and h<20) or (h>=150 and h<180)):
        result.append("빨강")

      #STEP2-2. 노랑에 대한 구분
      elif(h>=20 and h<37):
        result.append("노랑")

      #STEP2-3. 초록에 대한 구분(연두, 초록)
      elif(h>=37 and h<81):
        result.append("초록")

      #STEP2-4. 파랑에 대한 구분(청록, 파랑, 남색, 보라색, 자주색)
      elif(h>=81 and h<150):
        result.append("파랑")
    
    print("[Case3.색상] 색상 분석(H, S, V)")  
    print("[Case3-1.색상] 색상 분석(H) : "+str(h))
    print("[Case3-2.색상] 색상 분석(S) : "+str(s))    
    print("[Case3-3.색상] 색상 분석(V) : "+str(v))


    #텍스트 구하는 부분
    input_img = cv2.imread(rm_path, cv2.IMREAD_COLOR) #이미지 경로 변경 #이미지 경로에 rm_path 넣어야함
    bgr_img = mtjin_bgr2gray(input_img)
    #cv2_imshow(bgr_img)
    cv2.imwrite(sys.argv[1] + "\\\\gray.jpg", bgr_img)
    histogram_equalization(sys.argv[1] + "\\\\gray.jpg")
    ocr(sys.argv[1] + "\\\\histogram_eq.jpg")

    print("[Case5.최종 분석결과]")

    for i in result:
        print(i)
    #print(str(result))

if __name__ == "__main__":
	  main()

