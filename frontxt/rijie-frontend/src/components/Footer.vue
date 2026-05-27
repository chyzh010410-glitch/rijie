<template>
  <footer class="footer">
    <div class="footer-content">
      <div class="app-intro">
        <h2>日结兼职APP</h2>
        <p>支付宝投资的零工平台</p>
      </div>
      <div class="qrcode-group">
        <div class="qrcode-item">
          <img src="@/assets/images/02.jpg" alt="关注公众号" class="qrcode-img" />
          <p>关注公众号</p>
        </div>
        <div class="qrcode-item">
          <img src="@/assets/images/02.jpg" alt="下载APP" class="qrcode-img" />
          <p>下载 APP</p>
        </div>
        <div class="qrcode-item">
          <img src="@/assets/images/02.jpg" alt="微信小程序" class="qrcode-img" />
          <p>微信小程序</p>
        </div>
      </div>
    </div>
    <div class="copyright">
      <p>友情链接： 家救 上海环保展 金博教育 华图教育 同城招工找活</p>
      <p>Copyright © 2024 / 上海安心人力资源有限公司 / ICP 证书号：沪ICP备2021028424号-7 / 营业执照 / 人力资源服务许可证</p>
    </div>
  </footer>
</template>

<script setup>
// 页脚暂无额外逻辑
</script>

<style scoped>
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