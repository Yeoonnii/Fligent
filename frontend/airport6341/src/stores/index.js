import { createStore } from 'vuex';

export default createStore({
    state() {
        let tmp = false;
        if(sessionStorage.getItem("token") !== null){
            tmp = true;
        }
        return {
            logged  : tmp,
            counter : 1,
            imageurl : ''
        }
    },

    getters : { // App.vue에서 값을 가져 감
        getLogged : state => {
            return state.logged;
            
        },
        getCounter : state => {
            return state.counter;
        },
        getImageurl : state => {
            return state.imageurl;
        }
    },

    mutations:{ // LoginPage.vue, LogoutPage.vue
        setLogged(state, value){
            state.logged = value;
        },
        setCounter(state) {
            state.counter++;
        },
        setImageurl(state, value) {
            state.imageurl = value;
        }
    },

    actions:{ //벡엔드 연동 시간이 필요한 작업들..
        // 토큰을 이용해서 토큰이 유효한지 확인
    }
});

