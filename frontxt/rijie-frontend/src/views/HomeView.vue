<template>
  <el-backtop :right="100" :bottom="100" />
  <div class="home-page">
    <!-- 引入页头组件 -->
    <Header />

    <!-- Banner区域 -->
    <section class="banner">
      <div class="banner-text">
        <h1>专注于蓝领行业零工平台</h1>
        <p>帮企业招聘靠谱的临时工</p>
        <p>为蓝领提供便捷灵活就业</p>
      </div>
      <div class="banner-img">
        <img src="https://kmrijie.cn/assets/images/km/banner.png" alt="工人形象" />
      </div>
    </section>

    <!-- 数据统计 -->
    <section class="stats">
      <el-row :gutter="0" class="stats-row">
        <el-col :span="8">
          <div class="stat-card">
            <p class="stat-label">招工找活岗位</p>
            <p class="stat-value">100+</p>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-card">
            <p class="stat-label">蓝领临时工</p>
            <p class="stat-value">3000+</p>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-card">
            <p class="stat-label">服务人次</p>
            <p class="stat-value">1000+</p>
          </div>
        </el-col>
      </el-row>
    </section>

  <!-- 搜索模块 -->
  <!-- <SearchBar /> -->

    <!-- 功能介绍 -->
    <section class="features">
      <h2>迅速匹配，百万临时工，全行业覆盖</h2>
      <el-row :gutter="20" class="feature-cards">
        <el-col :xs="24" :sm="12" :md="6">
          <el-card class="feature-card">
            <div class="feature-icon">
              <el-icon size="30"><User /></el-icon>
            </div>
            <h3>海量临时工</h3>
            <p>百万临时工在日结兼职系统找工作</p>
            <el-button type="default" size="small">立即咨询</el-button>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-card class="feature-card">
            <div class="feature-icon">
              <el-icon size="30"><Timer /></el-icon>
            </div>
            <h3>招工效率高</h3>
            <p>70%的老板5分钟内招到人</p>
            <el-button type="default" size="small">立即咨询</el-button>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-card class="feature-card">
            <div class="feature-icon">
              <el-icon size="30"><Connection /></el-icon>
            </div>
            <h3>行业覆盖广</h3>
            <p>电子厂/五金厂/仓储物流/……</p>
            <el-button type="default" size="small">立即咨询</el-button>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-card class="feature-card">
            <div class="feature-icon">
              <el-icon size="30"><Avatar /></el-icon>
            </div>
            <h3>员工可信赖</h3>
            <p>不爽约、不迟到</p>
            <el-button type="default" size="small">立即咨询</el-button>
          </el-card>
        </el-col>
      </el-row>
    </section>

    <!-- 蓝领领域专家 -->
    <section class="expert">
      <h2>蓝领领域专家：一站式便捷产品咨询</h2>
      <el-row :gutter="20" class="expert-cards">
        <el-col :xs="24" :sm="8">
          <div class="expert-card">
            <img src="https://picsum.photos/id/1012/300/200" alt="团队背景" class="expert-img" />
            <h3>团队背景</h3>
            <p>产研技术先进，产品使用便捷</p>
            <el-button link class="expert-btn">立即咨询 →</el-button>
          </div>
        </el-col>
        <el-col :xs="24" :sm="8">
          <div class="expert-card">
            <img src="https://picsum.photos/id/1016/300/200" alt="产品经验" class="expert-img" />
            <h3>产品经验</h3>
            <p>深耕蓝领，领域10年+</p>
            <el-button link class="expert-btn">立即咨询 →</el-button>
          </div>
        </el-col>
        <el-col :xs="24" :sm="8">
          <div class="expert-card">
            <img src="https://picsum.photos/id/1025/300/200" alt="用户服务" class="expert-img" />
            <h3>用户服务</h3>
            <p>产品咨询，使用全面跟踪</p>
            <el-button link class="expert-btn">立即咨询 →</el-button>
          </div>
        </el-col>
      </el-row>
    </section>

    <!-- 客户信任 -->
    <section class="testimonials">
      <h2>来自客户的信任</h2>
      <el-row :gutter="20" class="testimonial-cards">
        <el-col :xs="24" :sm="8">
          <div class="testimonial-card">
            <p class="testimonial-content">仓库每天都要用很多日结工，去市场招人又费事又麻烦，在日结兼职随时招人来干活，省了很多事。</p>
            <p class="testimonial-author">张老板 比特精密有限公司</p>
          </div>
        </el-col>
        <el-col :xs="24" :sm="8">
          <div class="testimonial-card">
            <p class="testimonial-content">仓库每天都要用很多日结工，去市场招人又费事又麻烦，在日结兼职随时招人来干活，省了很多事。</p>
            <p class="testimonial-author">王主管 通达仓储物流有限公司</p>
          </div>
        </el-col>
        <el-col :xs="24" :sm="8">
          <div class="testimonial-card">
            <p class="testimonial-content">日结兼职上招工方便又好用，不像其他网站要填写很多无关信息招人又慢，发布信息之后很快就招到人了。</p>
            <p class="testimonial-author">刘老板 华天工艺品有限公司</p>
          </div>
        </el-col>
      </el-row>
    </section>

    <!-- 引入页脚组件 -->
    <Footer />
  </div>
</template>

<script setup>
import { Download, User, Timer, Connection, Avatar} from '@element-plus/icons-vue';
// 引入布局组件
import Header from '@/components/Header.vue';
import Footer from '@/components/Footer.vue';
import SearchBar from '@/components/SearchBar.vue'; // 引入搜索模块

</script>

<style scoped>

  
.home-page {
  font-family: "PingFang SC", "Microsoft YaHei", sans-serif;
  color: #333;
}

/* 顶部导航 */


/* 给页面主体添加顶部间距，避免内容被导航栏遮挡 */
.home-page {
  font-family: "PingFang SC", "Microsoft YaHei", sans-serif;
  color: #333;
  padding-top: 0px; /* 与导航栏高度一致 */
  width: 100vw; /* 核心：占满视口宽度 */
}

.logo-area {
  display: flex;
  align-items: center;
}

.logo-img {
  width: 40px;
  height: 40px;
  margin-right: 10px;
}

.logo-text {
  font-size: 20px;
  font-weight: bold;
  color: #333;
}

.el-menu-demo {
  flex: 1;
  justify-content: flex-start;
  background: transparent; 
  border: none; 
}

.el-menu-item {
  color: #000 !important;
}

.download-btn {
  background-color: #ffc107;
  border-color: #ffc107;
  color: #333;
  font-weight: bold;
}

.download-btn:hover {
  background-color: #e0a800;
  border-color: #e0a800;
}

/* Banner区域 */
.banner {
  display: flex;
  align-items: center;
  justify-content: space-around;
  padding: 100px 0px 0px 0px;
  background: linear-gradient(180deg,#76a9e3, #ffffff);
  /* background: linear-gradient(to bottom, #fff9e6, #ffffff); */
}

.banner-text {
  max-width: 600px;
}

.banner-text h1 {
  font-size: 48px;
  font-weight: bold;
  margin-bottom: 20px;
  color: #333;
}

.banner-text p {
  font-size: 26px;
  color: #666;
  margin-bottom: 10px;
}

.banner-img img {
  width: 492px;
  height: 428px;
  border-radius: 8px;
}

/* 数据统计 */
.stats {
 position: relative;   /* 基于原位置调整 */
  top: -60px;          /* 向上移动60px，可根据需求调整数值 */
  z-index: 10;         /* 确保层级高于Banner图片（Banner区域z-index建议低于10） */
  background-color: #fff; /* 保留白色背景，避免内容与Banner图片重叠后可读性差 */
  padding: 0px 40px 40px 40px;  /* 可根据上移幅度微调内边距，保持布局紧凑 */
}

.stats-row {
  justify-content: center;
}

.stat-card {
  text-align: center;
  padding: 20px 0;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  
}

.stat-label {
  font-size: 16px;
  color: #666;
  margin-bottom: 10px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
}

/* 功能介绍 */
.features {
  padding: 60px 40px;
  background-color: #f8f9fa;
}

.features h2 {
  font-size: 28px;
  font-weight: bold;
  text-align: center;
  margin-bottom: 40px;
  color: #333;
}

.feature-cards {
  justify-content: center;
}

.feature-card {
  text-align: center;
  padding: 30px;
  transition: all 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.feature-icon {
  margin-bottom: 20px;
  color: #333;
}

.feature-card h3 {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 10px;
  color: #333;
}

.feature-card p {
  font-size: 14px;
  color: #666;
  margin-bottom: 20px;
}

/* 蓝领领域专家 */
.expert {
  padding: 60px 40px;
  background-color: #fff;
}

.expert h2 {
  font-size: 28px;
  font-weight: bold;
  text-align: center;
  margin-bottom: 40px;
  color: #333;
}

.expert-cards {
  justify-content: center;
}

.expert-card {
  text-align: center;
  padding: 20px;
  border-radius: 8px;
  background-color: #fff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.expert-img {
  width: 100%;
  height: 150px;
  object-fit: cover;
  border-radius: 8px;
  margin-bottom: 15px;
}

.expert-card h3 {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 10px;
  color: #333;
}

.expert-card p {
  font-size: 14px;
  color: #666;
  margin-bottom: 20px;
}

.expert-btn {
  color: #333;
  font-size: 14px;
}

/* 客户信任 */
.testimonials {
  padding: 60px 40px;
  background-color: #f8f9fa;
}

.testimonials h2 {
  font-size: 28px;
  font-weight: bold;
  text-align: center;
  margin-bottom: 40px;
  color: #333;
}

.testimonial-cards {
  justify-content: center;
}

.testimonial-card {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.testimonial-content {
  font-size: 14px;
  color: #666;
  margin-bottom: 15px;
  line-height: 1.8;
}

.testimonial-author {
  font-size: 14px;
  font-weight: bold;
  color: #333;
  text-align: right;
}

/* 页脚 */
.footer {
  background-color: #333;
  color: #fff;
  padding: 40px;
}

.footer-content {
  display: flex;
  justify-content: space-around;
  align-items: center;
  margin-bottom: 30px;
}

.app-intro h2 {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 10px;
}

.app-intro p {
  font-size: 16px;
  color: #ddd;
}

.qrcode-group {
  display: flex;
  gap: 20px;
}

.qrcode-item {
  text-align: center;
}

.qrcode-img {
  width: 120px;
  height: 120px;
  margin-bottom: 10px;
}

.qrcode-item p {
  font-size: 14px;
  color: #ddd;
}

.copyright {
  text-align: center;
  font-size: 12px;
  color: #aaa;
  line-height: 1.8;
}
</style>