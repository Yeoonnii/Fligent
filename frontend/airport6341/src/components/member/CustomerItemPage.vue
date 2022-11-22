<template>
    <div class="container">
        <div class="item"> 
            <el-row>
                <div class="cate">
                    <select class="form-select" v-model="state.icateno"
                            style="float: right; width: 150px; margin-top: 10px;"
                            @change="onChange($event)">
                        <option selected value="1">인기아이템</option>
                        <option value="2">여행물품</option>
                        <option value="3">반려동물용품</option>
                        <option value="4">기내용물품</option>
                        <option value="5">여행생활용품</option>
                    </select>
                </div>
                <div class="itemcard">
                    <div class="card" v-for="obj of state.list" :key="obj">
                        <img style="height: 200px;" :src="obj.imageurl"  class="card-img-top"/>
                        <div class="card-body">
                        <h5 class="card-title">{{obj.name}}</h5><hr />
                        <p class="card-text"> ￦{{obj.price}}원/개당</p>
                        <div class="bottom">
                            <el-button text class="button" style="float: right; font-weight: 600; border:1px solid #cccccc; margin-top: 50px; margin-bottom: 20px;" @click="itemSelectOne(obj.ino)" > 상세보기 > </el-button>
                        </div>
                        </div>
                    </div>
                </div>
            </el-row>
        </div>
        <div class="page">
            <el-button v-for="tmp in state.pages" :key="tmp" @click="itemPages(tmp)">
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
        
        const state = reactive({
            list    : [],     
            start   : 1,
            page    : 1,
            pages   : 0,
            total   : 0,
            icateno : '1',
        });

        const handleData = async() => {
            const url = `/ROOT/api/item/selectitem.json?page=${state.page}&icateno=${state.icateno}`;
            const headers = {"Content-Type":"application/json"};
            const body = {}
            const {data} = await axios.get(url, body, headers);
            console.log(data);
            if(data.status === 200){
                state.list = data.itemlist;
                state.total = data.count;
                state.pages = Math.floor(data.count/12+1);
                for(const item of state.list){
                    item.price = item.price.toString().replace(/\B(?<!\.\d*)(?=(\d{3})+(?!\d))/g, ",");
                }
            }
        };

        const onChange = async(event) => {
            state.icateno = event.target.value;
            console.log(event.target.value);
            handleData();
        };

        const itemPages = (tmp) =>{
            state.page = tmp;
            handleData();
        };        

        const itemSelectOne = (ino) => {
            console.log("ino => ", ino);
            router.push({path:'/customeritemselectone', query:{ino : ino}});
        }

        onMounted(() => {
            handleData();
        });

        return {
            state,
            itemPages,
            itemSelectOne,
            onChange
        };
    }
}
</script>

<style lang="css" scoped>
.container{
    padding:30px;
    /* border:1px solid #cccccc; */
}
.cate{
    width: 89%;
    /* border:1px solid #cccccc; */
}
/* .item{
    border:1px solid #cccccc;
} */
.page{
    margin: auto;
    width: 90%;
    /* border:1px solid #cccccc; */
}
.itemcard{
    /* border:1px solid #cccccc; */
    padding: 50px;
}
.card:hover {
	opacity: 0.9;
	animation: post-ani 0.8s linear;
}
@keyframes post-ani {
  25% {
    transform: scale(1.03);
    }
}
.card{
  width: 250px; 
  border:1px solid #ffffff; 
  margin:10px; 
  float: left;
}
.card-title{
    font-weight: 600;
    font-family: 'IBMPlexSansKR-Regular';
}
.card-text{
    font-size: 20px;
}
@font-face {
    font-family: 'IBMPlexSansKR-Regular';
    src: url('https://cdn.jsdelivr.net/gh/projectnoonnu/noonfonts_20-07@1.0/IBMPlexSansKR-Regular.woff') format('woff');
    font-weight: normal;
    font-style: normal;
}
</style>