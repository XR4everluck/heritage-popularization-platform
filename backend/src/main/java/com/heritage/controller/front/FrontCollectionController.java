package com.heritage.controller.front;

import com.heritage.common.Result;
import com.heritage.service.UserCollectionService;
import com.heritage.vo.CollectionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 前台收藏接口：添加收藏、取消收藏、我的收藏列表
 *
 * <p>说明：用户身份暂由前端传 userId 参数（调试模式），后续接入 JWT 后替换。</p>
 */
@RestController
@RequestMapping("/api/collection")
@RequiredArgsConstructor
public class FrontCollectionController {

    private final UserCollectionService userCollectionService;

    /**
     * 添加收藏（重复收藏返回业务错误；成功后非遗收藏数 +1）
     *
     * @param userId     用户ID（调试模式）
     * @param heritageId 非遗项目ID
     */
    @PostMapping
    public Result<Void> add(@RequestParam Long userId, @RequestParam Long heritageId) {
        userCollectionService.add(userId, heritageId);
        return Result.ok();
    }

    /**
     * 取消收藏（未收藏时返回业务错误；成功后非遗收藏数 -1）
     *
     * @param userId     用户ID（调试模式）
     * @param heritageId 非遗项目ID
     */
    @DeleteMapping
    public Result<Void> remove(@RequestParam Long userId, @RequestParam Long heritageId) {
        userCollectionService.remove(userId, heritageId);
        return Result.ok();
    }

    /**
     * 查询我的收藏列表（含非遗名称/封面/级别/地区，按收藏时间倒序）
     *
     * @param userId 用户ID（调试模式）
     */
    @GetMapping("/my")
    public Result<List<CollectionVO>> my(@RequestParam Long userId) {
        return Result.ok(userCollectionService.listMy(userId));
    }
}
