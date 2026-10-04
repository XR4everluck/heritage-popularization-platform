package com.heritage.controller.front;

import com.heritage.common.Result;
import com.heritage.service.UserCollectionService;
import com.heritage.util.AuthContext;
import com.heritage.vo.CollectionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 前台收藏接口：添加收藏、取消收藏、我的收藏列表（均需登录，用户ID取自 token）
 */
@RestController
@RequestMapping("/api/collection")
@RequiredArgsConstructor
public class FrontCollectionController {

    private final UserCollectionService userCollectionService;

    /**
     * 添加收藏（重复收藏返回业务错误；成功后非遗收藏数 +1）
     *
     * @param heritageId 非遗项目ID
     */
    @PostMapping
    public Result<Void> add(@RequestParam Long heritageId, HttpServletRequest request) {
        userCollectionService.add(AuthContext.getUserId(request), heritageId);
        return Result.ok();
    }

    /**
     * 取消收藏（未收藏时返回业务错误；成功后非遗收藏数 -1）
     *
     * @param heritageId 非遗项目ID
     */
    @DeleteMapping
    public Result<Void> remove(@RequestParam Long heritageId, HttpServletRequest request) {
        userCollectionService.remove(AuthContext.getUserId(request), heritageId);
        return Result.ok();
    }

    /**
     * 查询我的收藏列表（含非遗名称/封面/级别/地区，按收藏时间倒序）
     */
    @GetMapping("/my")
    public Result<List<CollectionVO>> my(HttpServletRequest request) {
        return Result.ok(userCollectionService.listMy(AuthContext.getUserId(request)));
    }
}
