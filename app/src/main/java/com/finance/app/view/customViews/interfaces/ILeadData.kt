package com.finance.app.view.customViews.interfaces

/**
 * Created by Vishal Rathi on 10/1/2019.
 */
interface ILeadData {
    fun register(observer: IObserver)
    fun unregister(observer: IObserver)
    fun notifyObservers()
}