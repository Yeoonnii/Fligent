<template>
    <div class="container">
        <h3>물품수정</h3>
        <div class="update">
            <el-form label-width="150px">
                <el-form-item label="상품번호">
                    <el-input v-model="state.ino" style="width:400px" readonly />
                </el-form-item>
                <el-form-item label="상품명">
                    <el-input v-model="state.name" style="width:400px"/>
                </el-form-item>
    
                <el-form-item label="상품설명">
                    <el-input v-model="state.content" type="textarea" style="width:400px;" rows="10" />
                </el-form-item>       
    
                <el-form-item label="가격" >
                    <el-input v-model="state.price" style="width:400px"/>
                </el-form-item>
    
                <el-form-item label="재고수량" >
                    <el-input v-model="state.quantity" style="width:400px"/>
                </el-form-item>
    
                <el-form-item>
                    <el-button round @click="handleUpdate()" type="primary" style="height: 35px; margin-left: 15px; margin-right: 190px; font-size: 20px; font-weight: 600;">수정하기</el-button>
                    <el-button round @click="handleList()" type="info" plain style="height: 35px; font-size: 20px; font-weight: 600;">취소</el-button>
                </el-form-item>
            </el-form>
        </div>
    </div>
</template>

<script>
import axios from 'axios';
import { onMounted} from '@vue/runtime-core'
import { reactive } from '@vue/reactivity';
import { useRoute, useRouter } from 'vue-router'
export default {
    setup () {
        const route = useRoute();
        const router = useRouter();

        const state = reactive({
            token: sessionStorage.getItem("token"),
            row  : '',
            name : '',
            content : '',
            price : '',
            quantity : '',
            ino : Number(route.query.ino),
        });

        const handleData = async() => {
            const url =`/ROOT/api/admin/item/selectoneitem.json?ino=${state.ino}`;
            const headers = {
                "Content-Type":"application/json",
                "TOKEN" : state.token
            };
            const { data } = await axios.get(url, {headers});
            console.log(data);
            console.log(data.item.name);

            if(data.status === 200) {
                state.name = data.item.name;
                state.content = data.item.content;
                state.price = data.item.price;
                state.quantity = data.item.quantity;
            }
        };

        const handleUpdate = async() => {
            const url =`/ROOT/api/admin/item/updatepost.json`;
            const headers = {
                "Content-Type":"application/json",
                "token" : state.token
            };
            const body = {
                ino :state.ino,
                name :state.name,
                content : state.content,
                price : state.price,
                quantity : state.quantity,
            };

            const { data } = await axios.post(url, body, {headers});
            console.log(data);
            if(data.status === 200){
                alert('정보가 변경되었습니다.');
                router.push({path:'/adminmypage'});
            }
            else{
                alert('다시 시도해 주세요.');     
            }
        };

        const handleList = () => {
            router.push({path:'/adminmypage'});
        }

        onMounted(()=>{
            handleData();
        });

        return {
            state,
            handleUpdate,
            handleList
        }
    }
}
</script>

<style lang="css" scoped>
.container{
    width: 80%;
    padding:50px;
    text-align:center;
    /* border:1px solid #cccccc; */
}
.update{
    width: 50%;
    margin:0 auto;
    /* border:1px solid #cccccc; */
}
.container h3{
    font-family: 'MapoFlowerIsland';
    font-weight: 600;
    font-size: 35px;
    margin-bottom: 50px;
}
@font-face {
    font-family: 'MapoFlowerIsland';
    src: url('https://cdn.jsdelivr.net/gh/projectnoonnu/noonfonts_2001@1.1/MapoFlowerIslandA.woff') format('woff');
    font-weight: normal;
    font-style: normal;
}
</style>