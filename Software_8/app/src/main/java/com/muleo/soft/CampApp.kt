//package com.muleo.soft
//
//import android.app.Application
//import android.util.Log
//import com.google.firebase.Timestamp
//import com.google.firebase.auth.ktx.userProfileChangeRequest
//import com.muleo.soft.entity.*
//import com.muleo.soft.util.AuthUtil
//import com.muleo.soft.util.FirebaseUtil
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.SupervisorJob
//import java.util.*
//
//class CampApp : Application() {
//
//    val applicationScope = CoroutineScope(SupervisorJob())
//
//    override fun onCreate() {
//        super.onCreate()
//        // 테스트용 데이타
////        applicationScope.launch {
//        val fb = FirebaseUtil.fireStore
//
//        val manager = User(
//            email = "cs.javah@kakao.com",
//            pw = "zxcvb2",
//            age = 23,
//            name = "manager",
//            phone = "01050931539",
//            isManager = true,
//            date = Timestamp.now(),
//        )
//        val test0 = User(
//            email = "kdb0841@naver.com",
//            pw = "zxcvb2",
//            age = 23,
//            name = "testIM",
//            phone = "01050931539",
//            isManager = false,
//            date = Timestamp.now(),
//        )
//
//
//        val camp2 = Camp(
//            id = "${UUID.randomUUID()}",
//            name = "학술정보관관",
//            latitude = 37.60212757312891,
//            longitude = 126.95525162074983,
//            address = "서울특별시 종로구 홍지문2길 20",
//            isEmpty = true,
//            imgPath = "https://www.smu.ac.kr/cms/plugin/editorImage.do?EwBgjAHA9GZQKgMwFrILYBMAOBFAGgNIBiAqogDIASA7gQF4DGAmgHQBSACgOJA",
//            accountNumber = "02-2287-5114",
//            info = "상명문화사(학술정보관), 학술정보지원팀",
//            date = Timestamp.now(),
//            won = 10L,
//        )
//
//        val camp3 = Camp(
//            id = "${UUID.randomUUID()}",
//            name = "월해관(종합관)",
//            latitude = 37.6035696376732,
//            longitude = 126.95653610147713,
//            address = "서울특별시 종로구 홍지문2길 20",
//            isEmpty = false,
//            imgPath = "https://smul.smu.ac.kr/AeqPhoto/photolink.do?p=10013",
//            accountNumber = "02-2287-5114",
//            info = "평생교육원, 무용예술전공, 조형예술전공, 생활예술전공, 음악학부, 평생교육원교학팀, 3D프린팅융합센터",
//            date = Timestamp.now(),
//            won = 10L,
//        )
//        val camp4 = Camp(
//            id = "${UUID.randomUUID()}",
//            name = "인문사회과학대학관(자하관)",
//            latitude = 37.60117439933503,
//            longitude = 126.95426981226649,
//            address = "서울특별시 종로구 홍지문2길 20",
//            isEmpty = false,
//            imgPath = "https://smul.smu.ac.kr/AeqPhoto/photolink.do?p=10014",
//            accountNumber = "02-2287-5114",
//            info = "교양대학, 한일문화콘텐츠전공, 역사콘텐츠전공, 가족복지학과, 지적재산권전공 등등...",
//            date = Timestamp.now(),
//            won = 10L,
//        )
////            val camp8 = Camp(
////                id = "${UUID.randomUUID()}",
////                name = "경영경제대학관(밀레니엄관)",
////                latitude = 37.60207372280016,
////                longitude = 126.95580654833276,
////                address = "서울특별시 종로구 홍지문2길 20",
////                isEmpty = false,
////                imgPath = "https://smul.smu.ac.kr/AeqPhoto/photolink.do?p=10021",
////                accountNumber = "02-2287-5114",
////                info = "경제금융학부, 경영학부, 융합경영학과, 글로벌경영학과",
////                date = Timestamp.now(),
////                won = 10L,
////            )
////            val camp9 = Camp(
////                id = "${UUID.randomUUID()}",
////                name = "문화예술관",
////                latitude = 37.60317774657144,
////                longitude = 126.95664391281697,
////                address = "서울특별시 종로구 홍지문2길 20",
////                isEmpty = false,
////                imgPath = "https://smul.smu.ac.kr/AeqPhoto/photolink.do?p=10022",
////                accountNumber = "02-2287-5114",
////                info = "상명아트센터",
////                date = Timestamp.now(),
////                won = 10L,
////            )
////            val camp12 = Camp(
////                id = "${UUID.randomUUID()}",
////                name = "미래백년관",
////                latitude = 37.60298784827965,
////                longitude = 126.95480378640754,
////                address = "서울특별시 종로구 홍지문2길 20",
////                isEmpty = false,
////                imgPath = "https://smul.smu.ac.kr/AeqPhoto/photolink.do?p=10037",
////                accountNumber = "02-2287-5114",
////                info = "교육혁신추진팀, 디앤에이 랩(DNA LAB.), ACE사업혁신추진팀, 국고사업운영팀 등등...",
////                date = Timestamp.now(),
////                won = 10L,
////            )
////            val camp14 = Camp(
////                id = "${UUID.randomUUID()}",
////                name = "사범대학관",
////                latitude = 37.60234807140155,
////                longitude = 126.95461165804257,
////                address = "서울특별시 종로구 홍지문2길 20",
////                isEmpty = false,
////                imgPath = "https://smul.smu.ac.kr/AeqPhoto/photolink.do?p=10001",
////                accountNumber = "02-2287-5114",
////                info = "국어교육과, 영어교육과, 교육학과, 수학교육과, 우편취급국",
////                date = Timestamp.now(),
////                won = 10L,
////            )
//        val camp20 = Camp(
//            id = "${UUID.randomUUID()}",
//            name = "제1공학관",
//            latitude = 37.601591156656575,
//            longitude = 126.95439695578543,
//            address = "서울특별시 종로구 홍지문2길 20",
//            isEmpty = true,
//            imgPath = "https://www.smu.ac.kr/cms/plugin/editorImage.do?EwBgjAHA9GZQogNwIYBFkBcCSAlAHgFYAyACgNYYCCArgLYBeAJgJ4B0AUiQOJA",
//            accountNumber = "02-2287-5114",
//            info = "애니메이션 전공, 핀테크*빅테이터융합*스마트생산 전공 등등...",
//            date = Timestamp.now(),
//            won = 10L,
//        )
////            val camp21 = Camp(
////                id = "${UUID.randomUUID()}",
////                name = "학생회관",
////                latitude = 37.601974108124104,
////                longitude = 126.9544759926861,
////                address = "서울특별시 종로구 홍지문2길 20",
////                isEmpty = true,
////                imgPath = "https://www.smu.ac.kr/cms/plugin/editorImage.do?EwBgjAHA9GZQ7gcQGIAUAW6ByBbAWgIaoCOAigMYAeAZgEoBWALgEIB0AUqokA",
////                accountNumber = "02-2287-5114",
////                info = "글로벌랭귀지센터, 학생복지팀, 장애학생지원센터, 학생상담센터, 대학일자리센터 등등...",
////                date = Timestamp.now(),
////                won = 10L,
////            )
//        val campList =
//            listOf<Camp>(camp2, camp3, camp4, camp20)
////                    camp8, camp9, camp12, camp14, camp20, camp21)
//
//        AuthUtil.auth.also { auth ->
//            // 인증
//            auth.createUserWithEmailAndPassword(manager.email!!, manager.pw!!)
//                .addOnCompleteListener { task ->
//                    if (!task.isSuccessful) {
//                        Log.d("CampApp", "인증 실패 1 ${task.exception}")
//                    } else {
//                        val updateProfile = userProfileChangeRequest {
//                            displayName = manager.name
//                        }
//                        task.result!!.user!!.updateProfile(updateProfile)
//                            .addOnCompleteListener {
//                                if (it.isSuccessful) {
//                                    Log.d("CampApp", "프로필 업데이트")
//                                }
//                            }
//                    }
//                }
//            auth.createUserWithEmailAndPassword(test0.email!!, test0.pw!!)
//                .addOnCompleteListener { task ->
//                    if (!task.isSuccessful) {
//                        Log.d("CampApp", "인증 실패 2 ${task.exception}")
//                    } else {
//                        val updateProfile = userProfileChangeRequest {
//                            displayName = test0.name
//                        }
//                        task.result!!.user!!.updateProfile(updateProfile)
//                            .addOnCompleteListener { task ->
//                                if (task.isSuccessful) {
//                                    Log.d("CampApp", "프로필 업데이트")
//                                }
//                            }
//                    }
//                }
//            Log.d("CampApp", "${auth.currentUser}")
//        }
//
//
//        // 0. 캠핑장 정보
//        campList.forEach {
//            fb.collection("camps")
//                .document(it.id!!)
//                .set(it)
//                .addOnCompleteListener {
//                    if (it.isSuccessful) {
//                        Log.d("CampApp", "캠핑장 정보 업로드 성공")
//                    } else {
//                        Log.d("CampApp", "캠핑장 정보 업로드 오류")
//                    }
//                }
//        }
//
//
//        // 1. 유저 정보
//        fb.collection("users")
//            .document("${manager.email}")
//            .set(manager)
//        fb.collection("users")
//            .document("${test0.email}")
//            .set(test0)
//
//
//        // 2. 게시판 정보
//        // 게시판 Document 추가
//        val docName = BoardType.values().map { it.name }
//        val docType = BoardType.values().map { it.ordinal }
//        fb.collection("boards").document(docName[0])
//            .set(Board(type = docType[0], name = docName[0]))
//        fb.collection("boards").document(docName[1])
//            .set(Board(type = docType[1], name = docName[1]))
//
//        // 글 Document 를 posts collection 에 추가
//        val boardsRef = fb.collection("boards")
//        // 공지
//        val np0 = Post(
//            id = "${UUID.randomUUID()}",
//            type = docType[0],
//            title = "컴퓨터과학과 휴게실 공사 마침.",
//            post = "다음과 같이 G204, G203의 공사를 마쳤습니다.",
//            date = Timestamp.now(),
//            like = 1,
//            dislike = 1,
//            userId = "cs.javah@kakao.com",
//            images = listOf(
//                "https://www.smu.ac.kr/_attach/image/2020/11/thumb_rIXopzvZQlxLJLqdARYf.jpg",
//                "https://www.smu.ac.kr/_attach/image/2020/11/thumb_JifIEofYxogGiNXgVKok.jpg"
//            ),
//            likesId = listOf(
//                "cs.javah@kakao.com"
//            ),
//            dislikesId = listOf(
//                "kdb0841@naver.com"
//            ),
//        )
//        boardsRef.document(docName[0]).collection("posts")
//            .document(np0.id!!)
//            .set(np0)
//        // 공지 댓글
//        val c0 = Comment(
//            id = "${UUID.randomUUID()}",
//            date = Timestamp.now(),
//            comment = "와 너무 좋아~",
//            userId = "kdb0841@naver.com"
//        )
//        fb.collection("boards").document(docName[0]).collection("posts")
//            .document(np0.id).collection("comments")
//            .document("${c0.id}")
//            .set(c0)
//        val c1 = Comment(
//            id = "${UUID.randomUUID()}",
//            date = Timestamp.now(),
//            comment = "참고로 테스트 글입니다.",
//            userId = "cs.javah@kakao.com"
//        )
//        fb.collection("boards").document(docName[0]).collection("posts")
//            .document(np0.id).collection("comments")
//            .document("${c1.id}")
//            .set(c1)
//
//        // 리뷰
//        val rp0 = Post(
//            id = "${UUID.randomUUID()}",
//            type = docType[1],
//            title = "등수 언제 나오는지 아는사람?",
//            post = "ㅈㄱㄴ",
//            date = Timestamp.now(),
//            like = 1,
//            dislike = 1,
//            userId = "kdb0841@naver.com",
//            images = listOf(
//                "https://cloudfront-ap-northeast-1.images.arcpublishing.com/chosun/UVBJZL3RXAB36BDSHVM3MW2WNY.jpg"
//            ),
//            likesId = listOf(
//                "kdb0841@naver.com"
//            ),
//            dislikesId = listOf(
//                "cs.javah@kakao.com"
//            ),
//        )
//        boardsRef.document(docName[1]).collection("posts")
//            .document(rp0.id!!)
//            .set(rp0)
//        // 리뷰 댓글
//
//        // 3. 예약
//        val r1 = Reserve(
//            id = "${UUID.randomUUID()}",
//            userId = test0.email,
//            count = 3L,
//            totalPrice = 3000L,
//            isAccept = false,
//            start = Timestamp.now(),
//            end = Timestamp.now(),
//            reserveDate = Timestamp.now(),
//            acceptDate = Timestamp.now(),
//            campName = camp20.name!!,
//        )
//        fb.collection("camps")
//            .document(camp20.id!!) //  제 1공학관의 예약 정보.
//            .collection("reserves")
//            .document(r1.id!!)
//            .set(r1)
//            .addOnCompleteListener {
//                if (it.isSuccessful) {
//                    Log.d("CampApp", "예약정보 업로드 성공")
//                } else {
//                    Log.d("CampApp", "예약정보 업로드 실패")
//                }
//            }
//
//
//    }
//
//
////    private fun getHashKey() {
////        var packageInfo: PackageInfo? = null
////        try {
////            packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
////        } catch (e: PackageManager.NameNotFoundException) {
////            e.printStackTrace()
////        }
////        if (packageInfo == null) Log.e("KeyHash", "KeyHash:null")
////        for (signature in packageInfo!!.signatures) {
////            try {
////                val md: MessageDigest = MessageDigest.getInstance("SHA")
////                md.update(signature.toByteArray())
////                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT))
////            } catch (e: NoSuchAlgorithmException) {
////                Log.e("KeyHash", "Unable to get MessageDigest. signature=$signature", e)
////            }
////        }
////    }
//
//}