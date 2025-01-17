import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import './App.css';
import { createPinia } from 'pinia'
import piniaPersistedState from 'pinia-plugin-persistedstate'
import Toast from "vue-toastification";
// Import the CSS or use your own!
import "vue-toastification/dist/index.css";

const app = createApp(App)
const pinia = createPinia();

pinia.use(piniaPersistedState);
app.use(router)
app.use(Toast, 
    {
        timeout: 2000,
        enter: "fade-enter-active",
        leave: "Vue-Toastification__bounce-leave-active",
        move: "fade-move",
        position: "bottom-right"
    })
app.use(pinia);

app.mount('#app')
