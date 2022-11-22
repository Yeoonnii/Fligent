<template>
    <div class="container">
        <h3 style="margin-bottom: 50px;">게시글 등록</h3>
        <el-form :model="form" label-width="120px">
            <el-form-item label="제목">
                <el-input v-model="state.title" style="width:400px;" :ref = "el => {form[0] = el}" />
            </el-form-item>
            <el-form-item label="내용">
                <el-input v-model="state.content" type="textarea" style="width:400px;" rows="10" :ref = "el => {form[1] = el}" />
            </el-form-item>
            <el-form-item label="해시태그">
                <el-button v-for="(obj) in state.hashtag" :key="obj" :color="obj.hcolor" @click="handlePush(obj.hno)">{{obj.name}}</el-button>
                <label style="margin:10px;"></label>
            </el-form-item>
            <el-form-item label="첨부이미지">
                <input type="file" name="filename" accept="image/gif, image/jpeg, image/png" multiple @change="handleFile($event)" style="margin-left: 70px;" />
            </el-form-item>
            <div style="margin-top:30px;">
                <el-button color="#F76868" link @click="handleInsert()" style="font-size: 20px; font-weight: 600; ">등록하기</el-button>
                <el-button color="#F76868" link @click="handleList()" style="font-size: 20px; font-weight: 600;">취소</el-button>
            </div>
        </el-form>
    </div>
</template>
<script>
import { reactive, ref  } from '@vue/reactivity'
import axios from 'axios';
import { useRouter } from 'vue-router';
import { onMounted } from '@vue/runtime-core';
export default {
    setup () {
        const router = useRouter();
        const form = ref([]);

        const state = reactive({
            file     : [],
            image    : null,
            title    : '이미지등록test',
            content  : '이미지등록test이미지등록test',
            token    : sessionStorage.getItem("token"),
            userid   : '',
            hashtag : [], // 해시태그의 번호를 받을 배열 선언
            selecthashtag : [], // 선택한 여러개의 해시태그 번호를 받을 배열
        });

        // const hashtag = [];
        // hashtag.push(state.hname);
        // hashtag.push(state.hcolor);
        
        //state.file의 변수에 수동으로 첨부 이미지 정보를 넣기
        // var fileaaa = '';
        const handleFile = (e) =>{
            if(e.target.files.length == 0 ) { // 첨부한 파일이 존재하지 않는 경우
                state.file = []; // 초기화
            }
            else { // 첨부한 파일이 존재하는 경우
                for(let i=0; i<e.target.files.length; i++){
                    var pathpoint = e.target.files[i].name.lastIndexOf('.');
                    var filepoint = e.target.files[i].name.substring(pathpoint+1,e.target.files[i].name.length);
                    var filetype = filepoint.toLowerCase();
                    if(filetype=='jpg' || filetype=='gif' || filetype=='png' || filetype=='jpeg' || filetype=='bmp') {
                        // 정상적인 이미지 확장자 파일일 경우 ...
                        state.file[i] = e.target.files[i];
                    } else {
                        alert('이미지 파일만 선택할 수 있습니다.');
                        return false;
                    }
                }
            }
        };


        // 게시글 등록(글정보만)
        const handleInsert = async () => {
            // 어쨌든 state.file.length < 1 인경우 state.file== null 인 경우와 같으니 null일때, 아닐때로 판단한다
            if(state.file.length === 0){
                alert('이미지는 반드시 한장이상 첨부되어야 합니다!');
                return false;
            }

            if(state.title===''){
                alert('제목을 입력하세요.');
                form.value[0].focus();
                return false;
            }
            
            if(state.content===''){
                alert('내용을 입력하세요.');
                form.value[1].focus();
                return false;
            }

            if(state.selecthashtag.length == 0){
                alert('해쉬태그를 최소 1개 선택해주세요.');
                return false;
            }

            const url = `/ROOT/api/board/insertboardone.json`;
            const headers = {
                "Content-Type":"application/json",
                "TOKEN" : state.token
            }
            const body = {   //userid 가져와서 닉네임 받아야함
                title    : state.title,
                content : state.content, // 이미지를 표시하는 태그만 포함
                userid   : state.userid
            }

            const {data} = await axios.post(url, body, {headers});
            if(data.status === 200) {
                handleIMGInsert(data.bno);
                handleInsertHashtag(data.bno);
            }
            else{
                alert('등록에 실패하였습니다.');
                router.push({path:'/boardwrite'});
            }   
        };


        // 이미지 등록 
        const handleIMGInsert = async(bno) => {
            const url = `/ROOT/api/board/boardinsertbatchimage.json`;
            const headers ={
                "Content-Type":"multipart/form-data"
            };
            // 이미지가 있는 경우  body 정보 담기
            let body = new FormData();
            for(let i=0; i<state.file.length; i++){
                body.append("file", state.file[i]);
                body.append("bno", bno);
            }
            const { data }  = await axios.post(url, body, headers);
            if(data.status == 200){
                alert('게시글이 등록되었습니다!')
                router.push({path:'/board'});
            } 
            else{
                alert('게시글 등록에 실패하였습니다.\n다시 시도해주세요');
            }
        };
        
        // 해시태그 DB 정보 가져오기
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
        
        
        // 해시태그 배열에 담기
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
            }
            else if(chk == 1){
                state.hashtag[hno-1].hcolor = clonecolor[hno-1].hcolor;
            }
        }

        // 해시태그 등록
        const handleInsertHashtag = async(bno) => {
        // 완료 - 게시글 등록시 해시태그 여러개 추가
        // 127.0.0.1:8080/ROOT/api/board/hashtagmapping.json
        // POSTMAN => [{"bno": 136,"hno":1}, {"bno": 136,"hno":6}, {"bno": 136,"hno":9}, {"bno": 136,"hno":10}, {"bno": 136,"hno":11}]
        const body = [];
            for(let i=0; i<state.selecthashtag.length; i++){
                body.push({"bno":bno, "hno":state.selecthashtag[i]});
            }
        const url = `/ROOT/api/board/hashtagmapping.json`;
        const headers = {"Content-Type":"application/json"};
        console.log("body",body[0]);
        const ret = await axios.post(url, body, {headers});
        console.log("ret",ret);
        

        }

        // 글목록으로 돌아가기
        const handleList = () => {
            router.push({path:'/board'});
        }

        onMounted(()=>{
            // 해시태그 정보 가져오기
            hasgtagData();
        });

        return {
            state, 
            form,
            handleInsert, 
            handleList,
            handleFile,
            handlePush,
            handleInsertHashtag
        };
    }
}
</script>

<style lang="css" scoped>
.container{
    width: 850px;
    padding: 70px;
    margin: 30px auto 0px auto;
}
</style>