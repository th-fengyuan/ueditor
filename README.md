# ueditor

#### 项目介绍
UEditor 是由百度「FEX前端研发团队」开发的所见即所得富文本web编辑器，具有轻量，可定制，注重用户体验等特点，开源基于MIT协议，允许自由使用和修改代码。在原有基础上增加Word导入功能，Word导入可以解析Word文档中的图片。[点击访问官网](http://ueditor.baidu.com/website/index.html)

#### 软件架构

```
batik-all-1.7.jar
commons-codec-1.9.jar
commons-fileupload-1.3.3.jar
commons-io-2.2.jar
cssparser-0.9.25.jar
dom4j-1.6.1.jar
freehep-graphics2d-2.3.jar
freehep-graphicsbase-2.3.jar
freehep-graphicsio-2.3.jar
freehep-graphicsio-emf-2.3.jar
freehep-graphicsio-tests-2.3.jar
freehep-io-2.2.2.jar
hamcrest-core-1.1.jar
json-20160810.jar
jsoup-1.8.3.jar
junit-4.10.jar
log4j-1.2.8.jar
ooxml-schemas-1.1.jar
openxml4j-1.0-beta.jar
org.apache.poi.xwpf.converter.core-1.0.6.jar
org.apache.poi.xwpf.converter.xhtml-1.0.6.jar
poi-3.13.jar
poi-ooxml-3.13.jar
poi-ooxml-schemas-3.13.jar
poi-scratchpad-3.13.jar
sac-1.3.jar
stax-api-1.0.1.jar
wmf2svg-0.9.5.jar
xml-apis-1.0.b2.jar
xml-apis-ext-1.3.04.jar
xmlbeans-2.3.0.jar
```


#### 安装教程

1. pom文件中缺少batik-all-1.7.jar，可以在WEB-INF/lib目录下找到。
2. 也可以点击链接下载所有jar包。链接:https://pan.baidu.com/s/1e4kAkG5_ygiApGJYBpsfWg 密码:1k8q
3. 可以下载已打包好的文件，然后引入所有的jar包，或者引入pom文件，然后单独引入batik-all-1.7.jar和ueditor.jar，ueditor.jar可以在ueditor/jsp/lib目录下找到。链接: https://pan.baidu.com/s/1BXVmbhbytppCXjIo2g9UUA 密码: p5ym

#### 使用说明

1. 官方文档：http://fex.baidu.com/ueditor/
2. 官方API：http://ueditor.baidu.com/doc/

### 联系方式
邮箱：th_fengyuan@163.com