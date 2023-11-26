<script setup>
import LightCard from "@/components/LightCard.vue";
import {
    Calendar,
    Clock,
    CollectionTag,
    Compass,
    Document,
    Edit,
    EditPen,
    Link,
    Picture,
    Microphone, CircleCheck, Star, FolderOpened, ArrowRightBold
} from "@element-plus/icons-vue";
import Weather from "@/components/Weather.vue";
import {computed, reactive, ref, watch} from "vue";
import {get} from "@/net";
import {ElMessage} from "element-plus";
import TopicEditor from "@/components/TopicEditor.vue";
import {useStore} from "@/store";
import axios from "axios";
import ColorDot from "@/components/ColorDot.vue";
import router from "@/router";
import TopicTag from "@/components/TopicTag.vue";
import TopicCollectList from "@/components/TopicCollectList.vue";

const store = useStore()

const weather = reactive({
    location: {},
    now: {},
    hourly: [],
    success: false
})
const editor = ref(false)
const topics = reactive({
    list: [],
    type: 0,
    page: 0,
    end: false,
    top: []
})
const collects = ref(false)

watch(() => topics.type, () => resetList(), {immediate: true})

const today = computed(() => {
    const date = new Date()
    return `${date.getFullYear()} 年 ${date.getMonth() + 1} 月 ${date.getDate()} 日`
})
get('/api/forum/top-topic', data => topics.top = data)
function updateList(){
    if(topics.end) return
    get(`/api/forum/list-topic?page=${topics.page}&type=${topics.type}`, data => {
        if(data) {
            data.forEach(d => topics.list.push(d))
            topics.page++
        }
        if(!data || data.length < 10)
            topics.end = true
    })
}

function onTopicCreate() {
    editor.value = false
    resetList()
}

function resetList() {
    topics.page = 0
    topics.end = false
    topics.list = []
    updateList()
}

navigator.geolocation.getCurrentPosition(position => {
    const longitude = position.coords.longitude
    const latitude = position.coords.latitude
    get(`/api/forum/weather?longitude=${longitude}&latitude=${latitude}`, data => {
        Object.assign(weather, data)
        weather.success = true
    })
}, error => {
    console.info(error)
    ElMessage.warning('位置信息获取超时，请检测网络设置')
    get(`/api/forum/weather?longitude=103.88&latitude=30.8`, data => {
        Object.assign(weather, data)
        weather.success = true
    })
}, {
    timeout: 3000,
    enableHighAccuracy: true
})
</script>

<template>
    <div style="display: flex;margin: 20px auto;gap: 20px;max-width: 900px">
        <div style="flex: 1">
            <light-card>
                <div class="create-topic" @click="editor = true">
                    <el-icon><EditPen/></el-icon> 点击发表主题...
                </div>
                <div style="margin-top: 10px;display: flex;gap: 13px;font-size: 18px;color: grey">
                    <el-icon><Edit /></el-icon>
                    <el-icon><Document /></el-icon>
                    <el-icon><Compass /></el-icon>
                    <el-icon><Picture /></el-icon>
                    <el-icon><Microphone /></el-icon>
                </div>
            </light-card>
            <light-card style="margin-top: 10px;display: flex;flex-direction: column;gap: 10px">
                <div v-for="item in topics.top" class="top-topic" @click="router.push(`/index/topic-detail/${item.id}`)">
                    <el-tag type="info" size="small">置顶</el-tag>
                    <div>{{item.title}}</div>
                    <div>{{new Date(item.time).toLocaleDateString()}}</div>
                </div>
            </light-card>
            <light-card style="margin-top: 10px;display: flex;gap: 7px">
                <div :class="`type-select-card ${topics.type === item.id ? 'active' : ''}`"
                     v-for="item in store.forum.types"
                     @click="topics.type = item.id">
                    <color-dot :color="item.color"/>
                    <span style="margin-left: 5px">{{item.name}}</span>
                </div>
            </light-card>
            <transition name="el-fade-in" mode="out-in">
                <div v-if="topics.list.length">
                    <div style="margin-top: 10px;display: flex;flex-direction: column;gap: 10px"
                         v-infinite-scroll="updateList">
                        <light-card v-for="item in topics.list" class="topic-card"
                                    @click="router.push('/index/topic-detail/'+item.id)">
                            <div style="display: flex">
                                <div>
                                    <el-avatar :size="30" :src="store.avatarUserUrl(item.avatar)"/>
                                </div>
                                <div style="margin-left: 7px;transform: translateY(-2px)">
                                    <div style="font-size: 13px;font-weight: bold">{{item.username}}</div>
                                    <div style="font-size: 12px;color: grey">
                                        <el-icon><Clock/></el-icon>
                                        <div style="margin-left: 2px;display: inline-block;transform: translateY(-2px)">
                                            {{new Date(item.time).toLocaleString()}}
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div style="margin-top: 5px">
                                <topic-tag :type="item.type"/>
                                <span style="font-weight: bold;margin-left: 7px">{{item.title}}</span>
                            </div>
                            <div class="topic-content">{{item.text}}</div>
                            <div style="display: grid;grid-template-columns: repeat(3, 1fr);grid-gap: 10px">
                                <el-image class="topic-image" v-for="img in item.images" :src="img" fit="cover"></el-image>
                            </div>
                            <div style="display: flex;gap: 20px;font-size: 13px;margin-top: 10px;opacity: 0.8">
                                <div>
                                    <el-icon style="vertical-align: middle"><CircleCheck/></el-icon> {{item.like}}点赞
                                </div>
                                <div>
                                    <el-icon style="vertical-align: middle"><Star/></el-icon> {{item.collect}}收藏
                                </div>
                            </div>
                        </light-card>
                    </div>
                </div>
            </transition>
        </div>
        <div style="width: 280px">
            <div style="position: sticky;top: 20px">
                <light-card>
                    <div class="collect-list-button" @click="collects = true">
                        <span><el-icon><FolderOpened /></el-icon> 查看我的收藏</span>
                        <el-icon style="transform: translateY(3px)"><ArrowRightBold/></el-icon>
                    </div>
                </light-card>
                <light-card style="margin-top: 10px">
                    <div style="font-weight: bold">
                        <el-icon><CollectionTag/></el-icon>
                        论坛公告
                    </div>
                    <el-divider style="margin: 10px 0"/>
                    <div style="font-size: 14px;margin: 10px;color: grey">
                        主要功能<br>
                        （1）用户个人信息管理，用户隐私设置，密码设置。<br>
                        （2）用户头像设置。<br>
                        （3）论坛贴列表展示，支持分类展示。<br>
                        （4）论坛发帖功能，支持富文本编辑器含图片插入等功能。<br>
                        （5）论坛评论功能，支持用户对帖子进行评论或是对评论进行嵌套评论。<br>
                        （6）帖子点赞和收藏功能。<br>
                        （7）当地实时天气展示。<br>
                        其他功能还在持续开发中
                    </div>
                </light-card>
                <light-card style="margin-top: 10px">
                    <div style="font-weight: bold">
                        <el-icon><Calendar/></el-icon>
                        天气信息
                    </div>
                    <el-divider style="margin: 10px 0"/>
                    <weather :data="weather"/>
                </light-card>
                <light-card style="margin-top: 10px">
                    <div class="info-text">
                        <div>当前日期</div>
                        <div>{{today}}</div>
                    </div>
                    <div class="info-text">
                        <div>当期IP地址</div>
                        <div>127.0.0.1</div>
                    </div>
                </light-card>
                <div style="font-size: 14px;margin-top: 10px;color: grey">
                    <el-icon><Link/></el-icon>
                    友情链接
                    <el-divider style="margin: 10px 0"/>
                </div>
                <div style="display: grid;grid-template-columns: repeat(2, 1fr);grid-gap: 10px;margin-top: 10px">
                    <div class="friend-link">
                        <el-image style="height: 100%" src="https://element-plus.org/images/js-design-banner.jpg"/>
                    </div>
                    <div class="friend-link">
                        <el-image style="height: 100%" src="https://tse4-mm.cn.bing.net/th/id/OIP-C.Npdf2Kqw-wRebyPOQ7VhUQHaEK?w=298&h=180&c=7&r=0&o=5&dpr=1.3&pid=1.7"/>
                    </div>
                    <div class="friend-link">
                        <el-image style="height: 100%" src="https://element-plus.org/images/sponsors/jnpfsoft.jpg"/>
                    </div>
                    <div class="friend-link">
                        <el-image style="height: 100%" src="data:image/jpeg;base64,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"/>
                    </div>
                </div>
            </div>
        </div>
        <topic-editor :show="editor" @success="onTopicCreate" @close="editor = false"/>
        <topic-collect-list :show="collects" @close="collects = false"/>
    </div>
</template>

<style lang="less" scoped>
.collect-list-button {
  font-size: 14px;
  display: flex;
  justify-content: space-between;
  transition: .3s;

  &:hover {
    cursor: pointer;
    opacity: 0.6;
  }
}

.top-topic {
  display: flex;

  div:first-of-type {
    font-size: 14px;
    margin-left: 10px;
    font-weight: bold;
    opacity: 0.8;
    transition: color .3s;

    &:hover {
      color: grey;
    }
  }

  div:nth-of-type(2) {
    flex: 1;
    color: grey;
    font-size: 13px;
    text-align: right;
  }

  &:hover {
    cursor: pointer;
  }
}

.type-select-card {
  background-color: #f5f5f5;
  padding: 2px 7px;
  font-size: 14px;
  border-radius: 3px;
  box-sizing: border-box;
  transition: background-color .3s;

  &.active {
    border: solid 1px #ead4c4;
  }

  &:hover {
    cursor: pointer;
    background-color: #dadada;
  }
}

.topic-card {
  padding: 15px;
  transition: scale .3s;

  &:hover {
    scale: 1.015;
    cursor: pointer;
  }

  .topic-content {
    font-size: 13px;
    color: grey;
    margin: 5px 0;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 3;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .topic-image {
    width: 100%;
    height: 100%;
    max-height: 110px;
    border-radius: 5px;
  }
}

.info-text {
  display: flex;
  justify-content: space-between;
  color: grey;
  font-size: 14px;
}

.friend-link {
  border-radius: 5px;
  overflow: hidden;
}

.create-topic {
  background-color: #efefef;
  border-radius: 5px;
  height: 40px;
  color: grey;
  font-size: 14px;
  line-height: 40px;
  padding: 0 10px;

  &:hover {
    cursor: pointer;
  }
}

.dark {
  .create-topic {
    background-color: #232323;
  }

  .type-select-card {
    background-color: #282828;

    &.active {
      border: solid 1px #64594b;
    }

    &:hover {
      background-color: #5e5e5e;
    }
  }
}
</style>
