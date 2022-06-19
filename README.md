### 서치필(Search Pill) : 이미지 분석 및 OCR 기술을 활용한 의약품 분석 애플리케이션
***

#### #1. 과제 개발 동기 및 관련 통계자료 조사
|과제 개발 동기 및 관련 통계자료 조사|
|:---|
|현대사회는 의료 및 제약분야가 나날이 발전하고 있고, 시중에는 매년 수많은 의약품들이 개발 및 보급되고 있다. 그리고 그에 따라 의약품에 대한 수요는 꾸준히 늘어나고 있다. 보건복지부 통계 기준 국민 1인당 의약품 판매액은 549,085원(2017), 559,874원(2018), 599,303원(2019)으로 매해 꾸준히 증가추세를 보였으며, 교육부에서 학생들을 대상으로 실시한 특수교육실태조사에서 1년간 약물 복용 여부를 조사한 결과, 전체 학생 중 31.6%(2017), 32.6%(2020)에 해당하는 학생들이 약물을 복용한 경험이 있다고 응답하였다. 이처럼 수요가 늘어남에 따라, 자신에게 처방된 약의 성분은 무엇이며 어떤 효능을 가지는지 궁금해 하는 소비자들은 적지 않다. 만약 누구나 손쉽게 약에 대한 정보를 얻을 수 있다면 다음과 같은 긍정적인 효과들을 얻을 수 있을 것이다. <br> <br> (1). 부작용이 존재할 수 있는 약 성분에 대해 피처방자측의 능동적인 대처가 가능하다. <br> (2). 피처방자가 자신에게 적합한 약 성분에 대한 정보 숙지가 가능하다. <br> (3). 궁금증 해소에 따른 피처방자측 만족도 상승을 견인할 수 있다.|

#### #2. 기존 서비스 분석결과 및 차별점 제시
|기존 서비스 분석결과 및 차별점 제시|
|:---|
|대부분의 사람들은 조제약이나 시중에서 판매하는 약을 보관할 때 사용 용도를 구분해서 보관하지 않으며, 일반 의약품 포장지에는 약의 효능이 적혀 있지만 혹여나 포장지를 잃어버린 경우에는 약이 어떠한 효능을 가지고 있는지 알 수가 없다. 또한 알약의 부작용에 대한 정보의 접근성이 낮아 약을 오용하는 상황이 발생하고 저시력자나 노인층이 알약 섭취 시 작은 글씨로 인해 약 정보를 얻기 어렵다. 기존 서비스에 대한 시장분석 결과, 현재 Google Play Store(Android) 및 App Store(IOS)에서는 한 개의 단일한 의약품(알약)의 정보를 사진촬영만으로 식별할 수 있는 서비스를 제공하지는 않는다는 것을 확인하였다. 본 프로젝트에서는 의약품의 특징량을 분석한 약 20,000여개의 의약품 분류 데이터셋을 활용하여 사용자가 스마트폰으로 직접 촬영한 의약품에 대한 정보를 간편하게 제공하고자 한다. 더 나아가 사용자가 검색한 의약품과 유사한 대체성분의 의약품을 추천하는 서비스를 제공할 수 있도록 할 것이다.|

#### #3. 이미지 분석 모델 특징량 선정
|이미지 분석 모델 특징량 선정|
|:---|
|![KeyPoint](https://user-images.githubusercontent.com/63866366/174474763-b87bb0a0-7a82-47e8-b433-3598ab49c554.png)| 

#### #4. 프로젝트 아키텍처 설계 및 구현
|프로젝트 아키텍처 설계 및 구현|
|:---|
|![Architecture](https://user-images.githubusercontent.com/63866366/174474796-ce59921f-876c-48ac-9028-6d109f9edec6.png)| 

#### #5. 사용자 인터페이스(UI, User Interface) 설계 및 구현
|사용자 인터페이스(UI, User Interface)(1)|
|:---|
|![User Interface(1)](https://user-images.githubusercontent.com/63866366/174473786-a8086026-2199-447c-886e-e73245e956ec.png)| 

<br>

|사용자 인터페이스(UI, User Interface)(2)|
|:---|
|![User Interface(2)](https://user-images.githubusercontent.com/63866366/174473950-1156880d-5d3a-4c79-a3dd-9c614deded28.png)|

#### #6. 애플리케이션 사용 방법 및 기능
|애플리케이션 사용 방법 및 기능(1)|
|:---|
|![User Interface(3)](https://user-images.githubusercontent.com/63866366/174474364-69f7934c-a467-414a-814f-650c47101ee6.png)| 

<br>

|애플리케이션 사용 방법 및 기능(2)|
|:---|
|![User Interface(4](https://user-images.githubusercontent.com/63866366/174474419-b53a9752-0209-428a-b35b-b86b22dbc442.png)|

#### #7. 시스템 설계 목표 검증 및 분석
|이미지 분석모델 목표 정확도|
|:---|
|표본 100개를 대상으로 한 성능 분석에서, 다음의 정확도(Accuracy) 및 정밀도(Precision)를 얻을 수 있었습니다. <br> ※ OCR은 한글 각인을 가진 의약품이 상대적으로 부족함을 고려하여, 영문을 대상으로만 수행하였습니다. <br> ※ 테스트케이스의 다양성 및 종류가 충분히 확보되지 않았기에, 실제 정확도는 해당 수치보다 낮을 수 있습니다.| 
|![Accuracy](https://user-images.githubusercontent.com/63866366/174475285-644beda1-cbbd-4708-a7a3-a686337fe943.png)| 

<br>

|시스템 목표 성능 검증|
|:---|
|표본 10개를 대상으로 한 성능 분석에서, 다음의 평균처리시간을 얻을 수 있었습니다.|
|![Accuracy(1)](https://user-images.githubusercontent.com/63866366/174475320-685ce7ed-f7cf-4e88-9865-7d3605222cae.png)|

<br>

|시스템 목표 성능 통계 (단위 : 초)|
|:---|
|![Accuracy(2)](https://user-images.githubusercontent.com/63866366/174475378-49bf4d3e-0d40-48c8-8be9-3a0148b3ad26.png)
|
