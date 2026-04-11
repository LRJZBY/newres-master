package com.newsrec.manage.service.impl;


import java.util.List;
import com.newsrec.common.utils.DateUtils;
import com.newsrec.manage.domain.vo.BannerVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.newsrec.manage.mapper.BannerMapper;
import com.newsrec.manage.domain.Banner;
import com.newsrec.manage.service.IBannerService;

import javax.annotation.Resource;

/**
 * 轮播图Service业务层处理
 *
 * @author weiyan
 * @date 2025-02-20
 */
@Service
public class BannerServiceImpl implements IBannerService
{
    @Resource
    private BannerMapper bannerMapper;

    @Autowired
    private IBannerService bannerService;


    @Override
    public void clearOldBanners() {
        try {
            // 获取所有轮播图并按创建时间排序（从新到旧）
            List<Banner> allBanners = selectBannerList(null);
            if (allBanners == null || allBanners.isEmpty()) {
                System.out.println("No banners found, skipping cleanup.");
                return;
            }
            // 如果轮播图数量超过5条，删除多余的旧轮播图
            if (allBanners.size() > 5) {
                List<Banner> bannersToDelete = allBanners.subList(5, allBanners.size());
                // 提取要删除的轮播图ID，批量删除
                Long[] bannerIdsToDelete = bannersToDelete.stream().map(Banner::getBannerId).toArray(Long[]::new);
                int deletedCount = deleteBannerByBannerIds(bannerIdsToDelete);
                System.out.println("Deleted " + deletedCount + " old banners.");
            }
        } catch (Exception e) {
            System.err.println("Error cleaning old banners: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 查询轮播图
     *
     * @param bannerId 轮播图主键
     * @return 轮播图
     */
    @Override
    public Banner selectBannerByBannerId(Long bannerId)
    {
        return bannerMapper.selectBannerByBannerId(bannerId);
    }

    /**
     * 查询轮播图列表
     *
     * @param banner 轮播图
     * @return 轮播图
     */
    @Override
    public List<Banner> selectBannerList(Banner banner)
    {
        return bannerMapper.selectBannerList(banner);
    }

    /**
     * 新增轮播图
     *
     * @param banner 轮播图
     * @return 结果
     */
    @Override
    public int insertBanner(Banner banner)
    {
        banner.setCreateTime(DateUtils.getNowDate());
        return bannerMapper.insertBanner(banner);
    }

    /**
     * 修改轮播图
     *
     * @param banner 轮播图
     * @return 结果
     */
    @Override
    public int updateBanner(Banner banner)
    {
        banner.setUpdateTime(DateUtils.getNowDate());
        return bannerMapper.updateBanner(banner);
    }

    /**
     * 批量删除轮播图
     *
     * @param bannerIds 需要删除的轮播图主键
     * @return 结果
     */
    @Override
    public int deleteBannerByBannerIds(Long[] bannerIds)
    {
        return bannerMapper.deleteBannerByBannerIds(bannerIds);
    }

    /**
     * 删除轮播图信息
     *
     * @param bannerId 轮播图主键
     * @return 结果
     */
    @Override
    public int deleteBannerByBannerId(Long bannerId)
    {
        return bannerMapper.deleteBannerByBannerId(bannerId);
    }

    //轮播图vo
    @Override
    public List<BannerVo> selectBannerVoList(Banner banner) {
        return bannerMapper.selectBannerVoList(banner);
    }
}
