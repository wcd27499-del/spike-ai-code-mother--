package com.spike.spikeaicodemother.generate;

import cn.hutool.core.lang.Dict;
import cn.hutool.setting.yaml.YamlUtil;
import com.mybatisflex.codegen.Generator;
import com.mybatisflex.codegen.config.ColumnConfig;
import com.mybatisflex.codegen.config.GlobalConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.util.Map;

public class Codegen {

    //设置生成表的字符串数组
    private final static String[] TABLE_NAMES={"chat_history"};

    public static void main(String[] args) {
        //使用hutool工具包
        Dict dict = YamlUtil.loadByPath("application.yml");
        Map<String,Object> datasourceConfig = dict.getByPath("spring.datasource");
        String url = String.valueOf(datasourceConfig.get("url"));
        String username = String.valueOf(datasourceConfig.get("username"));
        String password = String.valueOf(datasourceConfig.get("password"));


        //配置数据源
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        //创建配置内容，两种风格都可以。
        GlobalConfig globalConfig = createGlobalConfig();


        //通过 datasource 和 globalConfig 创建代码生成器
        Generator generator = new Generator(dataSource, globalConfig);

        //生成代码
        generator.generate();
    }



    public static GlobalConfig createGlobalConfig() {

        //创建配置内容
        GlobalConfig globalConfig = new GlobalConfig();

        //设置根包
        globalConfig.getPackageConfig()
                .setBasePackage("com.spike.spikeaicodemother.genresult");

        //设置表前缀和只生成哪些表，setGenerateTable 未配置时，生成所有表
        globalConfig.getStrategyConfig()
                .setGenerateTable(TABLE_NAMES)
                .setLogicDeleteColumn("isDelete");//设置逻辑删除

        //设置生成 entity 并启用 Lombok
        globalConfig.enableEntity()
                .setWithLombok(true)
                .setJdkVersion(21);

        //设置生成 mapper
        globalConfig.enableMapper();
        globalConfig.enableMapperXml();
        //生成service
        globalConfig.enableService();
        globalConfig.enableServiceImpl();
        //生成controller
        globalConfig.enableController();

        //设置个人信息和时间配置
        globalConfig.getJavadocConfig()
                .setAuthor("spike")
                .setSince("");

        return globalConfig;
    }
}