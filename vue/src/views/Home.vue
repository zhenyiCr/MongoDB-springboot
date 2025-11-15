<template>
  <div class="card">
    HELLO
      <div>
          <el-timeline style="max-width: 600px">
              <el-timeline-item :timestamp="item.time" placement="top" v-for="item in data.noticeData" :key="item.id">
                      <h4>{{item.title}}</h4>
                      <p>{{item.content}}</p>
              </el-timeline-item>
          </el-timeline>
      </div>
  </div>
</template>
<script setup>
import {reactive} from 'vue'
import request from "@/utils/request.js";


const data = reactive({
    user: JSON.parse(localStorage.getItem('user') || '{}'),
    noticeData: []
})

const localNotice = () => {
    request.get('/notice/selectAll').then(res => {
        if (res.code === '200') {
            data.noticeData = res.data
            if (data.noticeData.length === 0) {
                ElMessage.info('暂无公告')
            }
            if (data.noticeData.length > 3) {
                data.noticeData = data.noticeData.slice(0, 3)
            }
        } else {
            ElMessage.error(res.msg)
        }
    })
}
localNotice()
</script>
