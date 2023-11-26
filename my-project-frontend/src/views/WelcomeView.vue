<template>
    <div style="width: 100vw;height: 100vh;overflow: hidden;display: flex">
        <div style="flex: 1">
            <el-image style="width: 100%;height: 100%" fit="cover"
                      src="https://ts1.cn.mm.bing.net/th/id/R-C.7db995bea71398ad81ca3b72531d375a?rik=QONUm1frqfvqbg&riu=http%3a%2f%2fpic.bizhi360.com%2fbbpic%2f77%2f3477.jpg&ehk=MyA5tLeOYZ4D9PHlB7i5jM8EF8rod1J31k%2fjnocTSe4%3d&risl=&pid=ImgRaw&r=0"/>
        </div>
        <div class="welcome-title">
            <div style="font-size: 30px;font-weight: bold">欢迎来到论坛</div>
            <div style="margin-top: 10px">在这里你可以和各位小伙伴交流学习、工作、生活</div>
            <div style="margin-top: 5px">在这里你还可以随心所欲交友，结交志同道合的人。</div>
        </div>
        <div class="right-card">
            <router-view v-slot="{ Component }">
                <transition name="el-fade-in-linear" mode="out-in">
                    <component :is="Component" style="height: 100%"/>
                </transition>
            </router-view>
        </div>
    </div>
</template>

<script setup>

</script>

<style scoped>
.right-card {
  width: 400px;
  z-index: 1;
  background-color: var(--el-bg-color);
}

.welcome-title {
    position: absolute;
    bottom: 30px;
    left: 30px;
    color: white;
    text-shadow: 0 0 10px black;
}
</style>
