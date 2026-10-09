package com.heritage.controller.front;

import com.heritage.common.Result;
import com.heritage.entity.HeritageNews;
import com.heritage.service.HeritageNewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * 前台科普快讯接口：首页快讯板块与快讯详情。
 * 数据源为后台「科普快讯管理」维护的 heritage_news，置顶快讯优先展示。
 */
@RestController
@RequestMapping("/api/heritage-news")
@RequiredArgsConstructor
public class FrontHeritageNewsController {

    private final HeritageNewsService heritageNewsService;

    /**
     * 快讯列表：置顶在前，其余按发布时间倒序，合并去重后截取 limit 条
     *
     * @param limit 返回条数，默认 6，最多 20
     */
    @GetMapping("/list")
    public Result<List<HeritageNews>> list(@RequestParam(required = false) Integer limit) {
        int size = (limit == null || limit <= 0) ? 6 : Math.min(limit, 20);

        // 置顶快讯最多10条（getTopNews 内部限制），其余按时间补齐
        List<HeritageNews> merged = new ArrayList<>(new LinkedHashSet<>(heritageNewsService.getTopNews()));
        if (merged.size() < size) {
            for (HeritageNews news : heritageNewsService.getLatestNews(size)) {
                if (merged.size() >= size) {
                    break;
                }
                boolean exists = merged.stream().anyMatch(item -> item.getId().equals(news.getId()));
                if (!exists) {
                    merged.add(news);
                }
            }
        }

        // 兜底排序：置顶优先，其次按发布时间倒序
        merged.sort(Comparator
                .comparing((HeritageNews news) -> news.getIsTop() == null ? 0 : news.getIsTop())
                .reversed()
                .thenComparing(HeritageNews::getPublishTime,
                        Comparator.nullsLast(Comparator.reverseOrder())));

        return Result.ok(merged.size() > size ? merged.subList(0, size) : merged);
    }

    /**
     * 快讯详情
     *
     * @param id 快讯ID
     */
    @GetMapping("/{id}")
    public Result<HeritageNews> detail(@PathVariable Long id) {
        HeritageNews news = heritageNewsService.getById(id);
        if (news == null || Integer.valueOf(1).equals(news.getDeleted())
                || !Integer.valueOf(1).equals(news.getStatus())) {
            return Result.error("快讯不存在或已下架");
        }
        return Result.ok(news);
    }
}
