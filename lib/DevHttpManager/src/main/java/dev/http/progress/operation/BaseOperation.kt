package dev.http.progress.operation

import android.os.Handler
import dev.DevUtils
import dev.http.progress.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient

/**
 * detail: 基础 Progress Operation 通用实现类
 * @author Ttt
 */
internal abstract class BaseOperation constructor(
    private val key: String,
    private val isDefault: Boolean,
    private val type: Int
) : IOperation {

    // 是否已调用 wrap 方法
    private var mUseWrap = false

    // 是否废弃不用 ( true 的情况下不会拦截网络且任何操作都不会赋值 )
    private var mDeprecated = false

    // 回调刷新时间 ( 毫秒 ) - 小于等于 0 则每次进度变更都进行通知
    private var mRefreshTime: Long = Progress.REFRESH_TIME

    // 全局 Progress.Callback
    private var mCallback: Progress.Callback? = null

    // 回调 UI 线程通知 ( 如果为 null 则会非 UI 线程通知 )
    private var mHandler: Handler? = null

    // 是否推荐请求一次 ( isOneShot() return 使用 ) 避免拦截器调用 writeTo 导致多次触发
    private var mOneShot: Boolean = true

    // 实现方案类型
    private var mPlanType: Int = ProgressOperation.PLAN_A

    // ==============
    // = IOperation =
    // ==============

    // ============
    // = 初始化方法 =
    // ============

    /**
     * 进行拦截器包装 ( 必须调用 )
     * @param builder Builder
     * @return Builder
     * 必须进行调用, 否则无法进行拦截监听
     * 推荐在 OkHttpClient Builder 最后一步调用
     * 防止中间有其他拦截器导致获取为旧数据
     * 例:
     * val okhttpClient = wrap(builder).build()
     */
    override fun wrap(builder: OkHttpClient.Builder): OkHttpClient.Builder {
        mUseWrap = true
        // 防止多次添加
        if (builder.interceptors().contains(innerProgressInterceptor)) {
            return builder
        }
        builder.addInterceptor(innerProgressInterceptor)
        return builder
    }

    // =============
    // = 对外公开方法 =
    // =============

    /**
     * 是否已调用 wrap 方法
     * @return `true` yes, `false` no
     */
    override fun isUseWrap(): Boolean {
        return mUseWrap
    }

    /**
     * 是否废弃不用状态
     * @return `true` yes, `false` no
     */
    override fun isDeprecated(): Boolean {
        return mDeprecated
    }

    /**
     * 是否默认操作对象
     * @return `true` yes, `false` no
     */
    override fun isDefault(): Boolean {
        return isDefault
    }

    /**
     * 是否监听上下行
     * @return `true` yes, `false` no
     */
    override fun isTypeAll(): Boolean {
        return type == ProgressOperation.TYPE_ALL
    }

    /**
     * 是否监听上行
     * @return `true` yes, `false` no
     */
    override fun isTypeRequest(): Boolean {
        return type == ProgressOperation.TYPE_REQUEST
    }

    /**
     * 是否监听下行
     * @return `true` yes, `false` no
     */
    override fun isTypeResponse(): Boolean {
        return type == ProgressOperation.TYPE_RESPONSE
    }

    // ===========
    // = get/set =
    // ===========

    /**
     * 获取 Progress Operation 实现方案类型
     * @return 实现方案类型
     */
    override fun getPlanType(): Int {
        return mPlanType
    }

    /**
     * 设置 Progress Operation 实现方案类型
     * @param planType 实现方案类型
     * @return IOperation
     */
    override fun setPlanType(planType: Int): IOperation {
        // 没有调用 wrap 前允许进行切换实现方案
        if (!mUseWrap) {
            mPlanType = when (planType) {
                ProgressOperation.PLAN_B -> {
                    planType
                }
                else -> {
                    ProgressOperation.PLAN_A
                }
            }
        }
        return this
    }

    // =

    /**
     * 获取回调刷新时间 ( 毫秒 )
     * @return 回调刷新时间 ( 毫秒 )
     */
    override fun getRefreshTime(): Long {
        return mRefreshTime
    }

    /**
     * 设置回调刷新时间 ( 毫秒 )
     * @param refreshTime 回调刷新时间 ( 毫秒 )
     * @return IOperation
     */
    override fun setRefreshTime(refreshTime: Long): IOperation {
        if (mDeprecated) return this
        mRefreshTime = refreshTime.coerceAtLeast(0)
        return this
    }

    /**
     * 重置回调刷新时间 ( 毫秒 )
     * @return IOperation
     */
    override fun resetRefreshTime(): IOperation {
        return setRefreshTime(Progress.REFRESH_TIME)
    }

    // =

    /**
     * 获取全局 Progress Callback
     * @return Progress Callback
     */
    override fun getCallback(): Progress.Callback? {
        return mCallback
    }

    /**
     * 设置全局 Progress Callback
     * @param callback Progress Callback
     * @return IOperation
     */
    override fun setCallback(callback: Progress.Callback?): IOperation {
        if (mDeprecated) return this
        mCallback = callback
        return this
    }

    /**
     * 移除全局 Progress Callback
     * @return IOperation
     */
    override fun removeCallback(): IOperation {
        return setCallback(null)
    }

    // =

    /**
     * 获取回调 UI 线程通知 Handler
     * @return 回调 UI 线程通知 Handler
     */
    override fun getHandler(): Handler? {
        return mHandler
    }

    /**
     * 设置回调 UI 线程通知 Handler
     * @param handler 回调 UI 线程通知 Handler
     * @return IOperation
     */
    override fun setHandler(handler: Handler?): IOperation {
        if (mDeprecated) return this
        mHandler = handler
        return this
    }

    /**
     * 重置回调 UI 线程通知 Handler
     * @return IOperation
     */
    override fun resetHandler(): IOperation {
        return setHandler(DevUtils.getHandler())
    }

    // =

    /**
     * 获取 Body 只请求一次开关配置
     * @return `true` yes, `false` no
     */
    override fun getOneShot(): Boolean {
        return mOneShot
    }

    /**
     * 设置 Body 只请求一次开关配置
     * @param oneShot Body 只请求一次开关配置
     * @return IOperation
     */
    override fun setOneShot(oneShot: Boolean): IOperation {
        if (mDeprecated) return this
        mOneShot = oneShot
        return this
    }

    /**
     * 重置 Body 只请求一次开关配置
     * @return IOperation
     */
    override fun resetOneShot(): IOperation {
        return setOneShot(true)
    }

    // ====================
    // = 操作方法 - 对外公开 =
    // ====================

    /**
     * 移除自身在 Manager Map 中的对象值, 并且标记为废弃状态
     * 会释放所有数据、监听事件且不处理任何监听
     */
    override fun removeSelfFromManager() {
        // 默认对象是不存储在 Map 中, 所以属于无效调用直接 return
        if (isDefault) return
        ProgressManager.removeOperation(key)
    }

    /**
     * 释放指定监听事件
     * @param progress Progress
     * @param callback 上传、下载回调接口
     * @return `true` success, `false` fail
     */
    override fun recycleListener(
        progress: Progress,
        callback: Progress.Callback
    ): Boolean {
        return removeListener(progress.isRequest(), getUrlByPrefix(progress), callback)
    }

    // ====================
    // = Request Listener =
    // ====================

    /**
     * 添加指定 url 上行 ( 上传、请求 ) 监听事件
     * @param url 请求 url
     * @param callback 上传、下载回调接口
     * @return `true` success, `false` fail
     */
    override fun addRequestListener(
        url: String,
        callback: Progress.Callback
    ): Boolean {
        return addListener(true, url, callback)
    }

    /**
     * 清空指定 url 上行 ( 上传、请求 ) 所有监听事件
     * @param url 请求 url
     * @return `true` success, `false` fail
     */
    override fun clearRequestListener(url: String): Boolean {
        return clearListener(true, url)
    }

    /**
     * 清空指定 url 上行 ( 上传、请求 ) 所有监听事件
     * @param progress Progress
     * @return `true` success, `false` fail
     */
    override fun clearRequestListener(progress: Progress?): Boolean {
        return clearListener(true, progress)
    }

    /**
     * 移除指定 url 上行 ( 上传、请求 ) 监听事件
     * @param url 请求 url
     * @param callback 上传、下载回调接口
     * @return `true` success, `false` fail
     */
    override fun removeRequestListener(
        url: String,
        callback: Progress.Callback
    ): Boolean {
        return removeListener(true, url, callback)
    }

    /**
     * 移除指定 url 上行 ( 上传、请求 ) 监听事件
     * @param progress Progress
     * @param callback 上传、下载回调接口
     * @return `true` success, `false` fail
     */
    override fun removeRequestListener(
        progress: Progress?,
        callback: Progress.Callback
    ): Boolean {
        return removeListener(true, progress, callback)
    }

    // =====================
    // = Response Listener =
    // =====================

    /**
     * 添加指定 url 下行 ( 下载、响应 ) 监听事件
     * @param url 请求 url
     * @param callback 上传、下载回调接口
     * @return `true` success, `false` fail
     */
    override fun addResponseListener(
        url: String,
        callback: Progress.Callback
    ): Boolean {
        return addListener(false, url, callback)
    }

    /**
     * 清空指定 url 下行 ( 下载、响应 ) 所有监听事件
     * @param url 请求 url
     * @return `true` success, `false` fail
     */
    override fun clearResponseListener(url: String): Boolean {
        return clearListener(false, url)
    }

    /**
     * 清空指定 url 下行 ( 下载、响应 ) 所有监听事件
     * @param progress Progress
     * @return `true` success, `false` fail
     */
    override fun clearResponseListener(progress: Progress?): Boolean {
        return clearListener(false, progress)
    }

    /**
     * 移除指定 url 下行 ( 下载、响应 ) 监听事件
     * @param url 请求 url
     * @param callback 上传、下载回调接口
     * @return `true` success, `false` fail
     */
    override fun removeResponseListener(
        url: String,
        callback: Progress.Callback
    ): Boolean {
        return removeListener(false, url, callback)
    }

    /**
     * 移除指定 url 下行 ( 下载、响应 ) 监听事件
     * @param progress Progress
     * @param callback 上传、下载回调接口
     * @return `true` success, `false` fail
     */
    override fun removeResponseListener(
        progress: Progress?,
        callback: Progress.Callback
    ): Boolean {
        return removeListener(false, progress, callback)
    }

    // ============
    // = abstract =
    // ============

    /**
     * 获取对应方案回调实现
     * @param extras 额外携带信息
     * @return Progress.Callback
     */
    internal abstract fun getPlanCallback(extras: Progress.Extras?): Progress.Callback

    /**
     * 添加指定 url 监听事件
     * @param isRequest `true` 上行 ( 上传、请求 ), `false` 下行 ( 下载、响应 )
     * @param url 请求 url
     * @param callback 上传、下载回调接口
     * @return `true` success, `false` fail
     * 会清空 url 字符串全部空格、Tab、换行符, 如有特殊符号需提前自行转义
     */
    internal abstract fun addListener(
        isRequest: Boolean,
        url: String,
        callback: Progress.Callback
    ): Boolean

    /**
     * 清空指定 url 所有监听事件
     * @param isRequest `true` 上行 ( 上传、请求 ), `false` 下行 ( 下载、响应 )
     * @param url 请求 url
     * @return `true` success, `false` fail
     */
    internal abstract fun clearListener(
        isRequest: Boolean,
        url: String
    ): Boolean

    /**
     * 清空指定 url 所有监听事件
     * @param isRequest `true` 上行 ( 上传、请求 ), `false` 下行 ( 下载、响应 )
     * @param progress Progress
     * @return `true` success, `false` fail
     */
    internal abstract fun clearListener(
        isRequest: Boolean,
        progress: Progress?
    ): Boolean

    /**
     * 移除指定 url 监听事件
     * @param isRequest `true` 上行 ( 上传、请求 ), `false` 下行 ( 下载、响应 )
     * @param url 请求 url
     * @param callback 上传、下载回调接口
     * @return `true` success, `false` fail
     */
    internal abstract fun removeListener(
        isRequest: Boolean,
        url: String,
        callback: Progress.Callback
    ): Boolean

    /**
     * 移除指定 url 监听事件
     * @param isRequest `true` 上行 ( 上传、请求 ), `false` 下行 ( 下载、响应 )
     * @param progress Progress
     * @param callback 上传、下载回调接口
     * @return `true` success, `false` fail
     */
    internal abstract fun removeListener(
        isRequest: Boolean,
        progress: Progress?,
        callback: Progress.Callback
    ): Boolean

    /**
     * 移除指定 url 监听事件
     * @param progress Progress
     * @param recycleList 待释放回调 List
     * @return `true` success, `false` fail
     */
    internal abstract fun removeRecycleList(
        progress: Progress,
        recycleList: List<Progress.Callback>
    ): Boolean

    // ==========
    // = 内部方法 =
    // ==========

    /**
     * 获取 Url 前缀 ( 去除参数部分 )
     * @param progress Progress
     * @return Url 前缀
     */
    internal fun getUrlByPrefix(progress: Progress?): String {
        return progress?.getExtras()?.getUrlExtras()?.urlByPrefix ?: ""
    }

    /**
     * 标记废弃不用状态
     * @return IOperation
     */
    internal fun markDeprecated(): IOperation {
        mDeprecated = true
        mCallback = null
        mHandler = null
        return this
    }

    /**
     * detail: 内部 Progress 拦截器
     * @author Ttt
     * DevHttpManager 库内部包装, 拦截监听上行 ( 上传、请求 )、下行 ( 下载、响应 ) 进度
     */
    private val innerProgressInterceptor: Interceptor by lazy {
        if (isTypeRequest()) {
            // 监听上行类型
            Interceptor { chain ->
                if (mDeprecated) {
                    chain.proceed(chain.request())
                } else {
                    val request = chain.request()
                    val extras = request.toExtras()
                    val wrapRequest = request.wrapRequestBody(
                        callback = getPlanCallback(extras),
                        handler = mHandler,
                        refreshTime = mRefreshTime,
                        shouldOneShot = mOneShot,
                        extras = extras
                    )
                    chain.proceed(wrapRequest)
                }
            }
        } else if (isTypeResponse()) {
            // 监听下行类型
            Interceptor { chain ->
                if (mDeprecated) {
                    chain.proceed(chain.request())
                } else {
                    val request = chain.request()
                    val extras = request.toExtras()
                    val response = chain.proceed(request)
                    response.wrapResponseBody(
                        callback = getPlanCallback(extras),
                        handler = mHandler,
                        refreshTime = mRefreshTime,
                        extras = extras
                    )
                }
            }
        } else {
            // 监听上下行类型 ( 默认 )
            Interceptor { chain ->
                if (mDeprecated) {
                    chain.proceed(chain.request())
                } else {
                    val request = chain.request()
                    val extras = request.toExtras()
                    val wrapRequest = request.wrapRequestBody(
                        callback = getPlanCallback(extras),
                        handler = mHandler,
                        refreshTime = mRefreshTime,
                        shouldOneShot = mOneShot,
                        extras = extras
                    )
                    val response = chain.proceed(wrapRequest)
                    response.wrapResponseBody(
                        callback = getPlanCallback(extras),
                        handler = mHandler,
                        refreshTime = mRefreshTime,
                        extras = extras
                    )
                }
            }
        }
    }
}