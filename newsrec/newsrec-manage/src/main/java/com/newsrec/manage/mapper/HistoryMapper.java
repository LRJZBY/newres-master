package com.newsrec.manage.mapper;

import java.util.List;
import com.newsrec.manage.domain.History;
import com.newsrec.manage.domain.vo.HistoryVo;

/**
 * 浏览历史Mapper接口
 *
 * @author weiyan
 * @date 2025-02-20
 */
public interface HistoryMapper
{
    /**
     * 查询浏览历史
     *
     * @param historyId 浏览历史主键
     * @return 浏览历史
     */
    public History selectHistoryByHistoryId(Long historyId);

    /**
     * 查询浏览历史列表
     *
     * @param history 浏览历史
     * @return 浏览历史集合
     */
    public List<History> selectHistoryList(History history);

    /**
     * 新增浏览历史
     *
     * @param history 浏览历史
     * @return 结果
     */
    public int insertHistory(History history);

    /**
     * 修改浏览历史
     *
     * @param history 浏览历史
     * @return 结果
     */
    public int updateHistory(History history);

    /**
     * 删除浏览历史
     *
     * @param historyId 浏览历史主键
     * @return 结果
     */
    public int deleteHistoryByHistoryId(Long historyId);

    /**
     * 批量删除浏览历史
     *
     * @param historyIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteHistoryByHistoryIds(Long[] historyIds);


    /**
     * 查询浏览历史列表Vo
     *
     * @param history 浏览历史
     * @return 浏览历史集合
     */
    public List<HistoryVo> selectHistoryVoList(History history);
}
