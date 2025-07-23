# SimpleWeather 프로젝트 규칙 및 개발 가이드

## 📘 프로젝트 정보

- **이름**: SimpleWeather
- **저장소**: [https://github.com/f-lab-edu/SimpleWeather](https://github.com/f-lab-edu/SimpleWeather)
- **설명**: Jetpack Compose와 Hilt, OpenWeatherMap API로 구현한 심플한 날씨 정보 앱입니다.
- **목표**: 최신 Android 기술 학습 및 포트폴리오 제작용

---

## 🛠️ 기술 스택

| 구분 | 내용 |
|------|------|
| 언어 | Kotlin |
| UI | Jetpack Compose, Material3 |
| 아키텍처 | MVVM (ViewModel, Repository, StateFlow 기반) |
| DI | Hilt |
| 네트워크 | Retrofit, Kotlin Serialization |
| API | OpenWeatherMap API |
| 최소 SDK | 28 (Android 9.0) |
| 빌드 | Gradle Kotlin DSL |
| 테스트 | (필요 시 도입) |

---

## 📁 프로젝트 구조 예시

app/
├── data/
│ ├── model/
│ ├── remote/
│ └── repository/
├── di/
├── features/
│ ├── main/
│ ├── detail/
│ └── search/
├── theme/
├── utils/
└── MyApplication.kt



---

## 🧭 개발 규칙

### 1. 커밋 단위

- 커밋은 반드시 **하나의 논리적인 변경사항**만 포함
- 커밋 메시지는 **한국어**로 작성
- 기능 단위로 명확하게 구분

#### 예시:

feat: 도시 검색 화면 UI 구성 (#5)

- 텍스트 필드 및 검색 버튼 추가
- 도시명 입력 시 키보드 제어 로직 구현
- 검색 결과를 리스트로 표시하는 뷰 추가

### 2. 작업 흐름
- 모든 작업은 이슈 기반으로 진행
- 사용자가 수동으로 등록한 이슈 번호를 기준으로 작업 (예: feature/#5-search-ui)
- 사용자가 특정 이슈 번호를 지정하면 그 작업만 수행
- Pull Request는 사용자가 명시적으로 요청할 때만 생성

### 3. 브랜치 네이밍

- 기능 추가	feature/#5-search-ui
- 버그 수정	fix/#12-null-crash
- 리팩토링	refactor/#8-main-card-ui

### 4. 언어 및 문서 작성 기준

- 커밋, 이슈, PR은 **모두 한국어로 작성**합니다.
- **코드 주석 및 문서도 한글로 통일**하여 관리합니다.

---

### 5. 푸시 및 리뷰 규칙

- 원격 저장소로 푸시하기 전 반드시 **코드 정리 및 테스트 완료**가 필요합니다.
- **Push 전 팀원 확인** 또는 **사용자 확인 요청**이 필수입니다.
- **PR은 요청받은 경우에만 생성**합니다.

---

### 6. GEMINI.md 관리 규칙

- `GEMINI.md` 파일은 **절대 커밋하거나 푸시하지 않습니다**.
- `.gitignore`에 반드시 포함되어야 합니다.


## 요약
- 이슈는 직접 등록 → 요청 시 해당 이슈만 작업합니다.

- PR은 요청받을 때만 생성합니다.

- 커밋, 브랜치, PR, 이슈 모두 한국어로 작성합니다.

- GEMINI.md는 절대 커밋 금지입니다.