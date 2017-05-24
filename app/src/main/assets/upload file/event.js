// ---------- 事件绑定与删除绑定 ---------- //
function bindEvent(element, eventName, func) {
    var events = element['the'+eventName];    //用于保存某个事件序列
    if(!events) {  //如果不存在一个序列，则创建它，并加入HTML标记当中的onEvent = function(){}形式的绑定
        events = element['the'+eventName] = [];
        if (element['on'+eventName]) { events.push(element['on'+eventName]); }
    }
    
    //检测是否为重复绑定
    for(var i=0; i<events.length; i++) {
        if(events[i] === func) { flag = true; break; }
    }
    
    // 非重复绑定，则加入该函数事件
    if(i >= events.length) { events.push(func); }
    
    // 重新定义这个事件的执行方式
    element['on'+eventName] = function(event) {
        event = event || (function() { //修复IE的事件对象
            var e = window.event;
            e.preventDefault = function() { e.returnValue = false; }
            e.stopPropagation = function() { e.cancelBubble = true; }
            //根据需要继续修复
            return e;
        })();
        //顺序执行这些函数
        for(var i=0; i<events.length; i++) { events[i].call(element, event); }
    }
}

// 删除事件绑定
function unBindEvent(element, eventName, func) {
    var events = this['the'+eventName];
    //如果不存在一个事件序列
    if(!events) { return false; }
    
    //检测该函数是否存在该事件序列当中
    for(var i=0; i<events.length; i++) {
        if(func === events[i]) {
            [].splice.call(events, i, 1);
            return true;
        }
    }
    
    // 函数不存在该事件序列当中
    return false;
}