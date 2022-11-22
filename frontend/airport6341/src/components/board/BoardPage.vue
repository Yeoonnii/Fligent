<template>
    <div class="container">
        <div class="item">
            <el-row>           
                <div class="cate">
                    <el-button @click="handleBoardWrite" style="font-size: 20px; width: 130px; margin-left: 20px; margin">글쓰기 <i class="bi bi-pencil-square"></i></el-button>
                    <el-input v-model="state.title" placeholder="검색할 제목입력 후 enter"
                        @keyup.enter="handleSearch()" style="width: 250px; float: right; margin-right: 40px;" />
                    <br />
                    <select class="form-select" v-model="state.type" 
                            style="float: right; width: 150px; margin-top: 10px; margin-right: 40px;"
                            @change="onChange($event)">
                        <option selected value="1">최신등록순</option>
                        <option value="2">조회수 순</option>
                        <option value="3">좋아요 순</option>
                        <option value="4">댓글수 순</option>
                    </select>
                    <div class="hash">
                        <el-button v-for="(obj) in state.hashtag" :key="obj" :color="obj.hcolor" @click="handlePush(obj.hno)"
                            style="font-size: 15px; font-weight: 600; margin: 10px;">#{{obj.name}}</el-button>
                    </div>
                    
                </div>
                <div class="card" v-for="obj of state.rows" :key="obj">
                    <img style="height: 200px;" :src="obj.imgurl" class="card-img-top"/>
                    <div class="card-body">
                        <h4 class="card-title" style="margin-top: 5px;">{{obj.title}}</h4>
                    </div>
                    <!-- <p class="card-text">{{obj.content}}</p> -->
                    <p class="card-text"><span style="color:#cccccc;">by. </span><span style="font-weight: 600;">{{obj.nickname}}</span></p>
                    <p class="card-text" style="font-size: 12px;">{{obj.regdate}}</p>
                    <p class="card-text" style="font-size: 15px;">
                        <img src="../../assets/free-animated-icon-heart-8121302.gif" style="width: 30px; margin-left: 100px;" />{{obj.likecount}}
                        <img src="../../assets/free-animated-icon-view-8121254.gif" style="width: 30px;" />{{obj.hit}}
                        <i class="bi bi-chat-left-dots fs-6" style="margin-left: 10px;"></i> {{obj.replycount}}
                    </p>
                    <div class="bottom" style="margin-bottom: 10px;">
                        <el-button text class="button" style="float: right; font-weight: 600;"  @click="boardSelectOne(obj.bno)" > 더보기 ></el-button>
                    </div>
                </div>
            </el-row>
            <el-button v-for="tmp in state.pages" :key="tmp" @click="boardPages(tmp)">
                {{tmp}}
            </el-button>
        </div>
    </div>
</template>

<script>
import { reactive } from '@vue/reactivity'
import { onMounted } from '@vue/runtime-core';
import axios from 'axios';
import { useRouter } from 'vue-router';
export default {
    setup () {
        const router = useRouter();

        // 상태 변수 생성 ( 담아서 밑에서 사용 )  // 버튼 만들때나 추가적으로 필요하면 state에 추가하기
        const state = reactive({
            hashtag       : [],
            selecthashtag : [],
            type    : 1, // type값 ( 1=최신순, 2=조회수 순, 3=좋아요 순, 4=댓글수 순 )
            rows    : null,    // 게시판 글목록        
            start   : 1,    // 현재의 페이지 번호(페이지네이션)
            total   : 0,  // 전체 페이지 수
            title   : '',   // 제목 검색어 
            content : '',   // 내용 검색어 
            page    : 1, 
            pages   : 0,
        });

        var clonecolor = [];
        const hasgtagData = async() => {
            const url = `/ROOT/api/board/hashtagdataget.json`
            const headers = {"Content-Type" : "application/json"}
            const {data} = await axios.get(url, headers)
            if(data.status == 200){
                state.hashtag = data.list;
                clonecolor = JSON.parse(JSON.stringify(data.list));
            }
        };
        const handlePush = (hno) => {
            var chk = 0;
            for(var j = 0; j<state.selecthashtag.length; j++){
                if(state.selecthashtag[j] == hno){
                    chk = 1;
                    state.selecthashtag.splice(j,1);
                    break;
                }
                else{
                    chk = 0;
                }
            }
            if(chk == 0){
                state.selecthashtag.push(hno);
                state.hashtag[hno-1].hcolor = '#cccccc';
                handleData();
            }
            else if(chk == 1){
                state.hashtag[hno-1].hcolor = clonecolor[hno-1].hcolor;
                handleData();
            }
        };

        // 벡엔드에서 데이터 가져오는 함수
        const handleData = async() => {
            if(state.selecthashtag.length == 0){state.selecthashtag.push(0);}
            else if(state.selecthashtag.length > 1){
                for(var j = 0; j<state.selecthashtag.length; j++){
                    if(state.selecthashtag[j] == 0){
                        state.selecthashtag.splice(j,1);
                        break;
                    }
                }
            }
            const url = `/ROOT/api/board/boardselectmain.json?type=${state.type}&page=${state.page}&title=${state.title}&content=${state.content}&hno=${state.selecthashtag}` 
            const headers = {"Content-Type":"application/json"};
            const body = {}
            const {data} = await axios.get(url, body, headers);
            if(data.status === 200){
                state.rows = data.result;
                state.total = data.count;
                state.pages = Math.floor((data.count-1)/12+1);
            }
            
        };

        const boardPages = (tmp) =>{
            state.page = tmp;
            handleData();
        };
        
        const onChange = async(event) => {
            state.type = event.target.value;
            // console.log(event.target.value);
            // console.log(state.icateno);
            handleData();
        };

        // 한개 게시글 클릭시 (상세페이지로 이동 + 조회 수 증가)
        const boardSelectOne = async(bno) => {
            // console.log("클릭된 게시글 번호 bno", bno);

            // 상세페이지로 이동시 조회수 1증가
            const url = `/ROOT/api/board/updatehit.json?bno=${bno}`;
            const headers = {"Content-Type":"application/json"};
            const { data } = await axios.put(url, {headers});
           
           if(data.status == 200){ //조회수 증가 완료된 경우
                router.push({path:'boardselectone', query:{bno:bno}});
            } else {
                alert('오류가 발생했습니다!\n다시 시도해주세요')
            }


        }

        // 페이지가 mount될때 자동 수행되는 생명주기
        onMounted(() => {
            handleData();
            hasgtagData();
        });

        // const handlePage = (start) => {
        //     // console.log('start',  start);
        //     state.start = start;
        //     handleData();
        // }

        // 제목 검색 기능 
        const handleSearch = () => {
            // console.log('검색한 제목 state.title =>', state.title);
            state.page = 1;
            handleData();
        }

        // 글쓰기 페이지로 이동 (이동전 로그인 여부 확인)
        const handleBoardWrite = () => {
            // console.log('sessionStorage.getItem("token") => ', sessionStorage.getItem("token"));

            if(sessionStorage.getItem("token") == null) {
                alert('로그인 후 작성 가능합니다!')
                router.push({path:'/memberlogin'});
            } else{
                router.push({path:'/boardwrite'});
            }
        }

        return {
            state, 
            handleSearch, 
            handleBoardWrite,
            boardPages,
            onChange,
            boardSelectOne,
            handlePush,
        };
    }
}
</script>

<style lang="css" scoped >
.container{
    width: 100vw;
    padding:30px;
}
.cate{
    width: 85vw;
    height: 150px;
    /* border:1px solid #cccccc; */
}
.hash{
    /* border:1px solid #cccccc; */
    width: 55%;
    margin-left: 17%;
}
.hash button:hover {
    /* opacity: ; */
	animation: post-ani 0.8s linear;
}
.card{
    width: 270px;
    margin: 20px;
    font-weight: 600;
    /* border:1px solid #ffffff;  */
}
.card-title{
  font-weight: 600;
  font-family: 'MapoFlowerIsland';
}
.card-text{
  font-weight: 500;
  margin-left: 20px;
}
.card:hover {
	opacity: 1.0;
	animation: post-ani 0.8s linear;
}
@keyframes post-ani {
  25% {
    transform: scale(1.03);
  }
}
</style>