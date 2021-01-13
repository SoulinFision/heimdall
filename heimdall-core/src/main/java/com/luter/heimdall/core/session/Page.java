/*
 *    Copyright 2020-2021 Luter.me
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.luter.heimdall.core.session;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 分页数据返回格式
 * <p>
 * 主要用作(redis 缓存)分页返回当前在线用户列表数据
 *
 * @param <T> the type parameter
 * @author Luter
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class Page<T> implements Serializable {
    /**
     * The Page size.
     */
    private Integer pageSize;
    /**
     * The Page number.
     */
    private Integer pageNumber;
    /**
     * The Total count.
     */
    private Long totalCount;
    /**
     * The Page count.
     */
    private Integer pageCount;
    /**
     * The Record count.
     */
    private Integer recordCount;
    /**
     * The Empty.
     */
    private Boolean empty;
    /**
     * The First.
     */
    private Boolean first;
    /**
     * The Last.
     */
    private Boolean last;
    /**
     * The Records.
     */
    private List<T> records;


    /**
     * 构造分页结果集
     *
     * @param pageNo     页码
     * @param size       每页数量
     * @param totalCount 总数
     * @param records    数据
     */
    public Page(int pageNo, int size, long totalCount, List<T> records) {
        this.pageNumber = pageNo;
        this.pageSize = size;
        this.records = records;
        this.totalCount = totalCount;
        this.pageCount = this.pageSize == 0 ? 1 : (int) Math.ceil((double) totalCount / (double) this.pageSize);
        this.first = this.pageNumber == 1;
        this.last = (this.pageNumber + 1) >= this.pageCount;
        this.empty = null == this.records || this.records.isEmpty();
        this.recordCount = null == this.records ? 0 : this.records.size();
    }


}