<template>
    <div class="container">
        <div class="item">
            <div class="row">
                <!-- {{state.row.imgurllist}} -->
                <div class="col-7">
                    <el-carousel indicator-position="outside" style="margin-top: 30px;">
                        <el-carousel-item v-for="obj in state.row.imgurllist" :key="obj">
                            <img :src="obj" style="width: 80%; height: 100%;" />
                        </el-carousel-item>
                    </el-carousel>
                    <h4 style="font-weight: 600;">{{state.row.title}}</h4><br />
                    
                    <div style="width: 100%; margin-bottom: 20px;">
                        <div style="float: right; margin-right: 60px; margin-left: 10px;">조회 {{ state.row.lcnt }}회</div>
                        <div style="float: right; margin-left: 180px;">좋아요 {{ state.row.hit }}개</div>
                        <span style="color:#cccccc;">by. </span>{{ state.row.nickname}}
                    </div>
                    <div style="width: 80%; margin: auto; font-size:18px; font-weight: 600;">
                        {{state.row.content}}
                    </div><br />
                    <span style="margin-bottom: 10px;">{{ state.row.regdate }}</span><br />
                    
                    <div v-if="state.row.userid == state.loginUserId" scope="col" style="width: 20%;">
                        <el-button round type="success" plain style="font-size: 12px; font-weight: 10; "
                            @click="handleBoardUpdate()">
                            수정
                        </el-button>
                        <el-button round type="warning" plain style="font-size: 12px; font-weight: 10; "
                            @click="handleBoardDelete()">
                            삭제
                        </el-button>
                    </div>
                    <div style="margin-top: 30px; width: 100%;">
                        <el-button link style="font-size: 17px; font-weight: 600; color: black; margin-right: 20px;" @click="handleBoardPage()">목록으로</el-button>
                        <el-button link @click="handlePrev()"><i class="bi bi-chevron-double-left fs-3" style="color:mediumblue;"></i></el-button>
                        <el-button link @click="handleLikePost()"><i class="bi bi-suit-heart fs-3" style="color:crimson;"></i></el-button>
                        <el-button link @click="handleNext()"><i class="bi bi-chevron-double-right fs-3" style="color:mediumblue;"></i></el-button>
                    </div>
                </div>

                <div class="col-5" style=" margin-top: 30px;">
                    <el-input type="text" style="width:80%; margin-right: 10px;" v-model="state.replyContent" placeholder="댓글을 남겨보세요"/>
                    <el-button link @click="handleInsertReply()"><i class="bi bi-pencil-square fs-3"></i></el-button>
                    <table class="table" style="width:100%; margin-top: 30px; height:10%;">
                        <thead>
                            <tr style="text-align: center;">
                                <th scope="col"></th>
                                <th scope="col"></th>
                                <th scope="col"></th>
                                <th scope="col"></th>
                            </tr>
                        </thead>
                        <tbody v-for="(obj) of state.result" :key="obj">
                            <td style="font-weight: 600;">{{ obj.nickname}}<span style="font-weight: 500;">님</span></td>
                            <td>"{{ obj.content }}"</td>
                            <td>{{ obj.regdate }}</td>
                            <!-- 사용자 본인의 댓글인 경우 삭제/수정버튼 보임 -->
                            <td v-if="obj.userid == state.loginUserId" scope="col" style="width: 20%;">
                                <el-button link type="success" plain style="font-size: 13px;"
                                    @click="handleReplyClick(obj.rno)"  
                                    data-toggle="modal" data-target="#myModal">
                                    수정{{ state.rno }}
                                </el-button>
                                <el-button link type="danger" plain style="font-size: 13px;"
                                    @click="handleReplyDelete()">
                                    삭제
                                </el-button>
                            </td>
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- 댓글 수정을 위한 모달창 생성 -->
            <!-- Modal -->
            <div class="modal hide" id="myModal" >
                <!-- 사용자 지정 부분① : id명 -->
                <div class="modal-dialog">
                    <!-- Modal content-->
                    <div class="modal-content">
                        <div class="modal-header">
                            <h4 class="modal-title">댓글 수정하기</h4> <!-- 사용자 지정 부분② : 타이틀 -->
                            <button type="button" class="close" data-dismiss="modal">×</button>
                        </div>

                        <div class="modal-body">
                            <!-- 댓글내용만 수정 -->
                            <p>댓글 수정 완료 후 수정버튼을 눌러주세요</p> <!-- 사용자 지정 부분③ : 텍스트 메시지 -->
                            <!-- :ref = "el => {form[0] = el}" => 1개니까 유효성검사는 그냥 아래서 바로 진행 -->
                            <el-input v-model="state.replyUpdate" style="width:60%"/>
                        </div>

                        <div class="modal-footer">
                            <button type="button" class="btn btn-default" @click="handleReplyUpdate()" >수정</button>
                            <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>
<script>
import { reactive } from '@vue/reactivity'
import { useRoute, useRouter } from 'vue-router'
import { onMounted } from '@vue/runtime-core';
import axios from 'axios';
export default {
    setup() {
        const route = useRoute();
        const router = useRouter();

        const state = reactive({
            bno         : Number(route.query.bno),
            rno         : '', //댓글번호
            row         : '', //상세 내용
            Prevno      : 0, //이전글번호
            Nextno      : 0, //다음글번호           
            //row1        : '', //수정 내용
            //imageurl    : '',
            //isUpdate    : false,
            hashtag     : [], //해시태그 배열
            replypage   : 1, //댓글 페이지네이션
            result      : '',//댓글 담기
            token       : sessionStorage.getItem("token"),
            likestatus  : '', // 좋아요 상태 확인 =>  A = 좋아요 등록됨 , B = 좋아요 해제됨
            replyContent: '', // 댓글 등록을 위한 변수
            replyUpdate : '', // 댓글 수정을 위한 변수
            loginUserId : '', //로그인한 사용자 아이디
            selectReply : null, //댓글수정을 위한 1개 조회
            likecolor   : ''
        });

        const handleReplyDelete = async(rno) => {
            if(confirm('삭제하시겠습니까?')){
                const url =`/ROOT/api/board/deletereply.json?rno=${rno}`;
                const headers = { "Content-Type": "application/json", "token": state.token };
                const body = {};
                const { data } = await axios.post(url, body, { headers });
                if (data.status === 200) {
                    handleData2();
                }
            }
        };

        const handlemodalclose = () => {
            let modal = document.getElementById("myModal")
            modal.style.display = 'none'
        };

        const handleBoardUpdate = (bno) => {
            router.push({path:'boardupdate', query:{bno:bno}});
        };

        const handleBoardDelete = async() => {
            if(confirm('삭제하시겠습니까?')){
                const url =`/ROOT/api/board/deleteoneboard.json?bno=${state.bno}`;
                const headers = { "Content-Type": "application/json", "token": state.token };
                const body = {};
                const { data } = await axios.post(url, body, { headers });
                if (data.status === 200) {
                    router.push({path:'/board'});
                }
            }
        };

        const handleData1 = async () => {
            // 1. 게시글 1개 정보 가져오기
            const url = `/ROOT/api/board/boardselectone.json?bno=${state.bno}`;
            const headers = { "Content-Type": "application/json" };
            const { data } = await axios.get(url, { headers });

            console.log('2222222222222 가져온 게시글 내용',data);
            if (data.status === 200) {
                state.row = data.result;
                state.Prevno = data.Prevno;
                state.Nextno = data.Nextno;
                // 로그인한 사용자 정보 조회
                handlecompareId();
            }
        };

        const handleData2 = async () => {
            // 2. 댓글 가져오기
            // 게시글 1개 조회시 해당 게시글에 등록된 댓글 목록 조회(8개씩 가져오기) + 게시글에 작성된 댓글 개수 COUNT
            // 127.0.0.1:8080/ROOT/api/board/boardselectonereply.json?bno=8&page=1
            const url1 = `/ROOT/api/board/boardselectonereply.json?bno=${state.bno}&page=${state.replypage}`;
            const headers1 = { "Content-Type": "application/json" };
            const response = await axios.get(url1, { headers1 });
            if (response.status == 200) {
                state.result = response.data.result;
            }
        };

        // 좋아요 버튼클릭시 좋아요수 1 증가/해제
        // 127.0.0.1:8080/ROOT/api/board/postlike.json + body에 {"board":{"bno": 57}}
        const handleLikePost = async () => {
            const url = `/ROOT/api/board/postlike.json`;
            const headers = {
                "Content-Type": "application/json",
                "token": state.token
            };
            const body = { board: { bno: state.bno } }
            const { data } = await axios.post(url, body, { headers });
            console.log('좋아요 버튼 클릭 결과 => ',data.likestatus)
            // A이면 좋아요 등록 B이면 좋아요 해제
            if (data.status == 200) {
                if (data.likestatus == 'A') {
                    alert('좋아요 등록됨')
                    state.likecolor = '#ffc0cb';
                } else {
                    alert('좋아요 해제됨')
                    state.likecolor = '';
                }
                handleData1();
            } else {
                alert('오류발생\n다시 시도해주세요')
            }
        };

        // 로그인한 사용자 정보 조회
        const handlecompareId = async () => {
            if (state.token == null) {
                state.loginUserId = null;
            } else {
                const url = `/ROOT/api/board/compareid.json`;
                const headers = {
                    "Content-Type": "application/json",
                    "token": state.token
                };
                const { data } = await axios.get(url, { headers });
                state.loginUserId = data.userid;
            }
        };

        // 댓글 등록하기
        // {"content":"11111" ,"board":{"bno": 57}}
        const handleInsertReply = async () => {
            if(state.token == null){
                alert('로그인 후 작성 가능합니다!')
                if(confirm('로그인 페이지로 이동하시겠습니까?')){
                    router.push({path:'/memberlogin'});
                }
                return;
            } else {
                // console.log('작성할 댓글 내용 ',state.replyContent);
                const url = `/ROOT/api/board/insertreply.json`;
                const headers = {
                    "Content-Type": "application/json",
                    "token": state.token
                }
                const body = {
                    content: state.replyContent,
                    board: { bno: state.bno }
                }
                const { data } = await axios.post(url, body, { headers });
                if (data.status == 200) {
                    alert('댓글이 등록되었습니다');
                    handleData2();
                }
            }
        };


        // 

        // 게시글 수정
        const handleUpdate = async () => {
            router.push({ path: '/boardupdate', query: { bno: state.bno } });
        };

        // 게시글 삭제
        const handleDelete = async () => {
            if (confirm('삭제할까요?')) {
                const url = `/ROOT/api/board/deleteoneboard.json?bno=${state.bno}`;
                const headers = { "Content-Type": "application/json" };
                const { data } = await axios.delete(url, { headers: headers });
                if (data.status === 200) {
                    router.push({ path: '/boardselect' });
                }
            }
            alert('게시물 삭제완료!');
        };

        // 댓글 수정버튼 클릭시 데이터 가져와서 모달창에 넣기
        const handleReplyClick = async (rno) => {
            state.rno = rno;

            // console.log('1111111111111 state.rno 에 담긴 rno 의 값 ====> ', state.rno)

            const url = `/ROOT/api/board/selectreply.json?rno=${state.rno}`;
            const headers = { "Content-Type": "application/json" };
            const { data } = await axios.get(url, { headers });
            // console.log('댓글 정보 조회 완료!!!!!!!!!!!!!!1 ',data)

            if (data.status == 200) {
                let modal = document.getElementById("myModal");
                // console.log('모달 불러오기 ')
                
                
                state.replyUpdate = data.result.content
                modal.style.display = 'block';
                // console.log('모달 보여주기 ')
            }
        }

        // 댓글 수정
        const handleReplyUpdate = async () => {
            // console.log('수정하기 버튼이 클릭됨!')
            // console.log('수정하려는 댓글번호', state.rno)
            // console.log('수정하려는 게시글번호', state.bno)
            // console.log('수정하려는 댓글내용 ', state.replyUpdate)

        // 완료 - 작성한 댓글 수정하기(사용자 일치여부 확인)
        // post => 토큰 + {"content":"11111" ,"board":{"bno": 57}, "rno":124}
        // 127.0.0.1:8080/ROOT/api/board/updatereply.json
            const url = `/ROOT/api/board/updatereply.json`;
            const headers = {
                "Content-Type":"application/json",
                "TOKEN" : state.token
            }
            const body = { 
                content  : state.replyUpdate, // 변경할 댓글내용
                board    : { bno: state.bno },
                rno   : state.rno
            }
            const {data} = await axios.post(url, body, {headers});
            // console.log('댓글수정 결과',data)
            if(data.status == 200){
                alert('댓글 수정이 완료되었습니다!')
                let modal = document.getElementById("myModal");
                modal.style.display = 'none';
                handleData2();
            }
        }

        // 이전글
        const handlePrev = async () => {
            // console.log('이전글 번호 => ', state.Prevno);
            router.push({ path: "/boardselectone", query: { bno: state.Prevno } });

            state.bno = state.Prevno
            handleData1();
            handleData2();
        };

        // 다음글
        const handleNext = async () => {
            // console.log('다음글 번호 => ', state.Nextno);

            router.push({ path: "/boardselectone", query: { bno: state.Nextno } });

            state.bno = state.Nextno
            handleData1();
            handleData2();
        };

        // 게시글 목록으로 이동
        const handleBoardPage = () => {
            router.push({path:'/board'});
        }

        onMounted(() => {
            handleData1();
            handleData2();
            // handlePrev();
            // handleNext();
        });

        return {
            state,
            handleDelete,
            handleUpdate,
            handleLikePost,
            handleInsertReply,
            handlePrev,
            handleNext,
            handlecompareId,
            handleReplyClick,
            handleReplyUpdate,
            handleBoardPage,
            handleBoardUpdate,
            handleBoardDelete,
            handlemodalclose,
            handleReplyDelete
        }
    }
}
</script>

<style lang="css" scoped>
.container {
    text-align: center;
    width: 100%;
    padding: 50px;
    border:1px solid #cccccc;
}
.item{
    margin: auto;
    border:1px solid #cccccc;
}
</style>