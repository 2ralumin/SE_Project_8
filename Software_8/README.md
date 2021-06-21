이 프로젝트는 firebase emulator(서버대용)를 통해 소통 하고 있습니다.
프로젝트를 실행할 수는 있으나 emulator가 실행중이 아닐 경우에는 로그인및 전체적인 동작이 불가능 해집니다.
firebase cli 를 통해 프로젝트 최상단에서 firebase init 명령어를 통해서 프로젝트 설정 및 emulator 설치가 필요합니다.
자세한 사항은 cs.javah@kakao.com 또는 
https://firebaseopensource.com/projects/firebase/firebase-tools/
를 참고하여 firestore, storage, authentication 을 사용하면 됩니다.
또한 자신의 firestore 프로젝트를 만들고 이를 init 명령어와 함께 넣어주어야 (--project 프로젝트이름) 앱이 정상적으로 동작합니다.
또한 emulator 이용시 port 설정에 주의를 요합니다. 
