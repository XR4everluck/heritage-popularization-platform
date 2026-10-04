package com.heritage;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heritage.entity.HeritageCategory;
import com.heritage.service.HeritageCategoryService;
import com.heritage.service.SysUserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 数据层冒烟测试：验证 MyBatis-Plus 的 CRUD、自增主键回填、逻辑删除与分页插件真实生效。
 *
 * <p>说明：测试直接使用 application.yml 配置的数据源；当数据库不可达
 * （如尚未修改 yml 中的账号密码占位符）时自动跳过，不会导致 mvn test 失败。</p>
 */
@SpringBootTest
class MyBatisPlusSmokeTest {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private HeritageCategoryService heritageCategoryService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void smokeCrudAndLogicDeleteAndPage() {
        // 数据库不可达时跳过整个测试（标记为 skipped 而非 failed）
        Integer ping;
        try {
            ping = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        } catch (Exception e) {
            Assumptions.assumeTrue(false, "数据库不可达，跳过数据层冒烟测试");
            return;
        }
        Assertions.assertNotNull(ping);

        // 1. 读取：种子用户数 >= 2（admin + user1），逻辑删除过滤自动生效
        long userCount = sysUserService.count();
        Assertions.assertTrue(userCount >= 2, "sys_user 种子数据应至少 2 条，实际：" + userCount);

        // 2. 新增：MP insert 后自增主键自动回填到实体
        HeritageCategory category = new HeritageCategory();
        category.setName("__stage3_smoke__");
        category.setSort(999);
        Assertions.assertTrue(heritageCategoryService.save(category), "新增分类应成功");
        Long id = category.getId();
        Assertions.assertNotNull(id, "自增主键应自动回填");

        // 3. 分页插件验证：单页 2 条，总数 >= 6（6 个种子分类）
        Page<HeritageCategory> page = heritageCategoryService.page(
                new Page<>(1, 2),
                new LambdaQueryWrapper<HeritageCategory>().orderByAsc(HeritageCategory::getSort));
        Assertions.assertTrue(page.getRecords().size() <= 2, "分页单页条数应 <= 2");
        Assertions.assertTrue(page.getTotal() >= 6, "分页总数应 >= 6，实际：" + page.getTotal());

        // 4. 逻辑删除：removeById 实际执行 UPDATE deleted=1，记录仍在库中
        heritageCategoryService.removeById(id);
        Integer deletedFlag = jdbcTemplate.queryForObject(
                "SELECT deleted FROM heritage_category WHERE id = ?", Integer.class, id);
        Assertions.assertEquals(1, deletedFlag, "逻辑删除后 deleted 应为 1");

        // 5. 逻辑删除后的记录对业务查询不可见（getById 自动拼接 deleted=0 条件）
        Assertions.assertNull(heritageCategoryService.getById(id), "逻辑删除后业务层查询应不可见");

        // 6. 物理清理测试数据（原生 SQL，带参数绑定）
        jdbcTemplate.update("DELETE FROM heritage_category WHERE id = ?", id);
    }
}
