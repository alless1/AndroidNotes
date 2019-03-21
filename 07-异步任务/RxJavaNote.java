package com.sankeyun.ecg.ui.activity;

import com.sankeyun.ecg.db.AppDatabase;
import com.sankeyun.ecg.db.entity.UserAccountEntity;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ${程杰} on 2018/4/2.
 * 描述:
 */

public class RxJavaNote extends BaseActivity {

    public void method1() {
        Observable
                /**
                 * create(ObservableOnSubscribe<T> source)创建一个完整的被观察者，可以处理各种操作，然后将对象发射
                 * just(T item)直接发送单个或多个参数item
                 * fromArray(T... items)直接发送数组或集合里的参数
                 * doOnNext()
                 */
                .create(new ObservableOnSubscribe<List<UserAccountEntity>>() {
                    @Override
                    public void subscribe(ObservableEmitter<List<UserAccountEntity>> e) throws Exception {
                        //数据库操作
                        List<UserAccountEntity> all = AppDatabase.getAppDatabase(mContext).getUserAccountDao().getAll();
                        //此处执行的onNext，onError，onComplete，分别都会在subscribe（）方法中调用。
                        e.onNext(all);
                    }
                })
                /**
                 * 将事件序列中的对象或整个序列进行加工处理，转换成不同的事件或事件序列
                 * map()一对一的转化。直接转换成结果对象
                 *flatMap(Function<? super T, ? extends ObservableSource<? extends R>> mapper) 一对多转化。转换成被观察者对象，可以继续转化。
                 */


/*                .map(new Function<List<UserAccountEntity>, Object>() {
                    @Override
                    public Object apply(List<UserAccountEntity> userAccountEntities) throws Exception {
                        return null;
                    }
                })*/
                //指定 subscribe() 发生在 IO 线程
                .subscribeOn(Schedulers.io())
                //指定 Observer 的回调发生在主线程
                .observeOn(AndroidSchedulers.mainThread())
                /**
                 *  subscribe(Observer<? super T> observer)创建一个完整的观察者Observer,里面有各种方法onNext,onComplete。
                 *  subscribe(new Consumer<List<UserAccountEntity>>())创建只有onNext方法的观察者。
                 */
                .subscribe(new Observer<List<UserAccountEntity>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<UserAccountEntity> value) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }
	
	//单纯的线程切换；
	Observable
                .empty()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        Log.e("Observer.empty()", "doOnCompleted");
                    }
                })
                .subscribe();
	
	//定时任务
	Observable.timer(2, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        hideProgress();
                        Intent intent = new Intent(YuYueEcgActivity.this, YuYueEcgDetailActivity.class);
                       // intent.putExtra("appointmentNo", "123456789");
                        intent.putExtra("bean", bean);
                        startActivity(intent);
                        finish();
                    }
                })
                .subscribe();


}
