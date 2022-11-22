<template>
    <div>
        <h3>게시판 수정</h3>
        {{ state.row.imgurllist }}
        <div>
            <!-- <div class="col-7">
                    <el-carousel indicator-position="outside">
                        <el-carousel-item v-for="obj in state.row.imgurllist" :key="obj">
                            <img :src="obj" style="width: 55%; height: 100%;"/>
                        </el-carousel-item>
                    </el-carousel>
                </div> -->

        </div>


        <div v-if="state.row">
            제목 : <input type="text" v-model="state.row.title" /><br />
            작성자 닉네임 : <input type="text" v-model="state.row.nickname" readonly /><br />
            내용 : <textarea rows="6" v-model="state.row.content"></textarea><br />
            <input type="button" value="수정" @click="handleUpdate()" />

            <label style="font-size: 30px; font-weight: 600;">첨부된 이미지</label><br />
            <!-- <el-scrollbar>
                    <div class="scrollbar-flex-content">
                      <p v-for="obj in state.row.imgurllist" :key="obj" class="scrollbar-demo-item"> 
                        <img :src="obj" alt="" style="width: 100%; height: 100%;" onchange="handlePreview"/> 
                      </p>
                    </div>
                 </el-scrollbar> -->


            <!-- 웹보드 -->
            <!-- <el-form>
            <el-form-item label="파일 추가하기">
                <el-upload :file-list="imgurllist" list-type="picture-card" :auto-upload="false">
                    <el-icon>
                        <Plus />
                    </el-icon>
            </el-upload> -->



                
            <!-- <el-upload
                v-for="obj in state.row.imgurllist" :key="obj"
                        action="https://run.mocky.io/v3/9d059bf9-4660-45f2-925d-ce80ad6c4d15"
                        list-type="picture-card"
                        >
                        <img :src="obj" alt="" style="width: 100%; height: 100%;" /> 
                            :on-remove="handleRemove"
            <el-icon><Plus /></el-icon>
                </el-upload>
            </el-form-item> -->
<!-- 
            <el-form-item>
                <el-button type="primary" @click="handleInsert">등록</el-button>
                <el-button>Cancel</el-button>
            </el-form-item> -->
        <!-- </el-form> -->

        <!-- <el-dialog v-model="dialogVisible">
            <img w-full :src="dialogImageUrl" style="width:100%" alt="Preview Image" />
        </el-dialog> -->

        </div>

        <hr />
        <div>















            <!-- <el-upload action="#" list-type="picture-card" :auto-upload="false">
                <el-icon><Plus /></el-icon>
                            
                <template file="state.row.imgurllist">
                  <div>
                    <img class="el-upload-list__item-thumbnail" :src="file.url" alt="" />
                    <span class="el-upload-list__item-actions">
                      <span
                        class="el-upload-list__item-preview"
                        @click="handlePictureCardPreview(file)"
                      >
                        <el-icon><zoom-in /></el-icon>
                      </span>
                      <span
                        v-if="!disabled"
                        class="el-upload-list__item-delete"
                        @click="handleDownload(file)"
                      >
                        <el-icon><Download /></el-icon>
                      </span>
                      <span
                        v-if="!disabled"
                        class="el-upload-list__item-delete"
                        @click="handleRemove(file)"
                      >
                        <el-icon><Delete /></el-icon>
                      </span>
                    </span>
                  </div>
                </template>
            </el-upload> -->

            <!-- <el-dialog v-for="obj in state.row.imgurllist" :key="obj" title="수정" width="50%">
                <img w-full :src="obj" alt="Preview Image" />
            </el-dialog> -->












            <!-- ================== -->
            <!-- <el-upload
                        v-model="state.row.imgurllist"
                        action="https://run.mocky.io/v3/9d059bf9-4660-45f2-925d-ce80ad6c4d15"
                        list-type="picture-card"
                        >
                        <p v-for="obj in state.row.imgurllist" :key="obj" > 
                            <img :src="obj" alt="" style="width: 100%; height: 100%;" :on-preview="handlePictureCardPreview"/> 
                        </p> -->
            <!-- :on-remove="handleRemove" -->
           <!-- <el-icon><Plus /></el-icon>
            </el-upload> -->

            <!-- <el-dialog v-model="state.row.imgurllist">
                        <img w-full :src="dialogImageUrl" alt="Preview Image" />
                      </el-dialog> -->
        </div>
    </div>
</template>

<script>
import { reactive, ref } from '@vue/reactivity';
import { useRoute, useRouter } from 'vue-router'
import { onMounted, watch } from '@vue/runtime-core';
import axios from 'axios';
// import type { UploadProps, UploadUserFile } from 'element-plus'

export default {

    setup() {
        const route = useRoute(); // 이전 페이지에 넘어온 값 받기
        const router = useRouter(); // 다른 페이지로 이동하기 위해 받음
        const state = reactive({
            bno: Number(route.query.bno),
            row: '',
            imgurllist: [],
            token: sessionStorage.getItem("token"),

        });

        // 특정 변수의 값이 변경 되면 실행
        watch(state, () => {
            console.log('watch =>', state);
        }, {
            immediate: true, // 즉시
            deep: true, // state 내부의 객체가 바뀌면
        });

        const handleData = async () => {
            console.log('수정하려는 글 번호', state.bno)
            // 원래 글 정보 받아오기
            const url = `/ROOT/api/board/boardselectone.json?bno=${state.bno}`;
            const headers = { "Content-Type": "application/json" };
            const { data } = await axios.get(url, { headers });
            console.log('수정페이지 가져온 기존 게시글 내용', data);
            if (data.status === 200) {

                state.row = data.result;
                // state.row = response.data.result;
                // state.imgurllist = response.data.result.imgurllist

                // console.log('******** 이미지 주소들 ********', state.row.imgurllist[0])
                // console.log('******** 이미지 주소들을 담은 배열 ********', state.imgurllist)
            }
        };

        // 게시글 수정하기
        const handleUpdate = async () => {
            // /ROOT/api/board/updateoneboard.json
            const url = `/ROOT/api/board/updateoneboard.json`;
            const headers = { "Content-Type": "application/json", "token": state.token };
            const body = {
                bno: state.bno,
                title: state.row.title,
                content: state.row.content
            };

            const response = await axios.put(url, body, { headers });
            console.log(response.data);
            if (response.data.status === 200) {
                alert('수정 완료!')
                router.push({path:'/board'});                
            }
        }

        // 이미지 변경시 미리보기
        const handlePreview = async () => {
            // input 태그 (name이 f인) 가져옴
            let fileTag = document.querySelector("input[name=f]");

            console.log('가져온 fileTag  ', fileTag)
        }

        // 이미지 업로드/미리보기를 위한 함수
        // =============================================
        const dialogImageUrl = ref('');
        // const dialogVisible = ref(false);
        // const disabled = ref(false)

        const handleRemove = (uploadFile, uploadFiles) => {
            console.log(uploadFile, uploadFiles);
        }

        const handlePictureCardPreview = (uploadFile) => {
            dialogImageUrl.value = uploadFile.url;
            //   dialogVisible.value = true;
        }
        // =============================================


        onMounted(() => {
            handleData();
        });

        return {
            state,
            handleUpdate,
            handlePreview,
            handleRemove,
            handlePictureCardPreview
        }
    }
}
</script>

<style lang="css" scoped>
.scrollbar-flex-content {
    display: flex;
}

.scrollbar-demo-item {
    flex-shrink: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 160px;
    height: 160px;
    margin: 1%;
    text-align: center;
    border-radius: 4px;
    background: var(--el-color-danger-light-9);
    color: var(--el-color-danger);
}
</style>