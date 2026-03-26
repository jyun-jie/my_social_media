<script setup>
import { reactive } from 'vue';
import axios from 'axios'; 
const userInfo = reactive({
  phoneNumber: '',
  password: '',
  email: '',
  userName: '', 
});


const submit = async () => {
  // 簡單的前端防呆驗證
  if (!userInfo.phoneNumber || !userInfo.password) {
    alert('手機號碼與密碼為必填欄位！');
    return;
  }

  try {
    const response = await axios.post('http://localhost:8080/api/auth/register', userInfo);

    if (response.data.code === 200) {
      alert('🎉 ' + response.data.message); 
    } else {
      alert('❌ 註冊失敗：' + response.data.message);
    }

  } catch (error) {
    
    console.error('API 請求失敗:', error);
    if (error.response && error.response.data) {
       alert('發生錯誤：' + (error.response.data.message || '伺服器異常'));
    } else {
       alert('無法連線到伺服器，請檢查後端是否啟動。');
    }
  }
};
</script>

<template>
  <div class="register-container">
    <p>手機號碼：<input type="text" v-model="userInfo.phoneNumber" placeholder="例如: 0912345678" /></p>
    <p>密碼：<input type="password" v-model="userInfo.password" placeholder="請設定密碼" /></p>
    <p>電子郵件：<input type="email" v-model="userInfo.email" placeholder="example@mail.com" /></p>
    <p>使用者名稱：<input type="text" v-model="userInfo.userName" placeholder="請輸入顯示名稱" /></p>
    
    <button @click="submit">提交註冊</button>
  </div>
</template>