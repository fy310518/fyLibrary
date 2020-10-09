# fyLibrary
常用工具集

gradle 远程依赖使用：
一：在项目根目录的 build.gradle 文件夹下 增加 maven { url 'https://jitpack.io' }

allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
}

二：在 对应的 library Module下的 build.gradle 文件 增加如下依赖，然后编译即可【tag 表示版本号，最新是 1.7.29】

dependencies {
	        implementation 'com.github.fy310518:fyLibrary:Tag'
}
