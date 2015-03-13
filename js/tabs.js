var tabNavigation = function() {
    this.init.apply(this, arguments);
};

tabNavigation.prototype = {

    settings : {},
    elements : {},

    init : function(settings) {
        var self = this;

        settings = this.arrayMerge({
            /*
            boxElementIdName  : ナビゲーションとして実装する要素の親要素のID名
            anchorElementName : ナビゲーションとして実装する要素の要素名
            linkAttrName      : ナビゲーションとして実装する要素の属性名（切り替える要素のID名が格納されている属性名）
            openClassName     : 表示時のCSSクラス名
            defaultTargetId   : 初期時に表示するタブのインデックス番号
            */
            boxElementIdName  : '',
            anchorElementName : 'a',
            linkAttrName      : 'href',
            openClassName     : 'open',
            defaultShowIndex  : 0
        }, settings);

        this.settings = settings;

        this.elements = {
            tabNaviBoxElement    : null,
            tabNaviAnchorElement : null
        };

        this.elements.tabNaviBoxElement = document.getElementById(settings.boxElementIdName);

        var tabNaviBoxElement     = this.elements.tabNaviBoxElement;
        var tabNaviAnchorElements = null;
        var attr                  = '';

        if (tabNaviBoxElement) {
            this.elements.tabNaviAnchorElements = tabNaviBoxElement.getElementsByTagName(settings.anchorElementName);

            tabNaviAnchorElements = this.elements.tabNaviAnchorElements;

            if (this.elements.tabNaviAnchorElements) {
                var clickEvent = function(event) {
                    self.changeElement(this.getAttribute(settings.linkAttrName));
                    if (event.preventDefault) {
                        event.preventDefault();
                    } else {
                        event.returnValue = false;
                    }
                    if (event.stopPropagation) {
                        event.stopPropagation();
                    } else {
                        event.cancelBubble = true;
                    }
                };

                var tabNaviAnchorElements = tabNaviBoxElement.getElementsByTagName(settings.anchorElementName);
                for (var i = 0, len = tabNaviAnchorElements.length; i < len; i++) {
                    tabNaviAnchorElements[i].onclick = clickEvent;
                    if (i === settings.defaultShowIndex) {
                        attr = tabNaviAnchorElements[i].getAttribute(settings.linkAttrName);
                        this.changeElement(attr, true);
                    }
                }

            }
        }
        return this;
    },

    changeElement : function(targetId, startFlag) {
        var attrHref      = '';
        var elements      = this.elements.tabNaviAnchorElements;
        var targetElement = null;
        var ii            = 0;
        var len2          = 0;
        var classNames    = '';

        if (targetId && targetId !== '' && targetId.match(/^#[A-Za-z]/)) {
            if (elements) {
                for (var i = 0, len = elements.length; i < len; i++) {
                    attrHref = elements[i].getAttribute(this.settings.linkAttrName);

                    if (attrHref && attrHref !== '' && attrHref.match(/^#[A-Za-z]/)) {
                        targetElement = document.getElementById(attrHref.replace('#', ''));

                        if (attrHref === targetId) {
                            if (elements[i].parentNode.className.indexOf(this.settings.openClassName) === -1) {
                                targetElement.style.display    = 'block';
                                targetElement.style.visibility = 'visible';

                                this.addClass(elements[i].parentNode);
                            }
                        } else {
                            if (startFlag || elements[i].parentNode.className.indexOf(this.settings.openClassName) !== -1) {
                                targetElement.style.display    = 'none';
                                targetElement.style.visibility = 'hidden';

                                this.removeClass(elements[i].parentNode);
                            }
                        }
                    }
                }
            }
        }
        return this;
    },

    addClass : function(obj) {
        if (obj) {
            if (obj.className.indexOf(this.settings.openClassName) === -1) {
                if (obj.className !== '') {
                    var classNames = obj.className.split(' ');
                    var hasClassName = false;
                    classNames.push(this.settings.openClassName);
                    obj.className = classNames.join(' ');
                } else {
                    obj.className = this.settings.openClassName;
                }
            }
        }
        return this;
    },

    removeClass : function(obj) {
        if (obj) {
            if (obj.className.indexOf(this.settings.openClassName) !== -1) {
                if (obj.className !== '') {
                    var classNames = obj.className.split(' ');
                    for (var i = 0, len = classNames.length; i < len; i++) {
                        if (classNames[i] === this.settings.openClassName) {
                            classNames.splice(i, 1);
                        }
                    }
                    obj.className = classNames.join(' ');
                    obj.className = obj.className.replace(this.settings.openClassName, '');
                }
                if (obj.className === '') {
                    obj.removeAttribute('class');
                }
            }
        }
        return this;
    },

    arrayMerge : function() {
        var key, result = false;
        if (arguments && arguments.length > 0) {
            result = {};
            for (var i = 0, len = arguments.length;i < len; i++) {
                if (arguments[i] && typeof arguments[i] === 'object') {
                    for (key in arguments[i]) {
                        if (isFinite(key)) {
                            result.push(arguments[i][key]);
                        } else {
                            result[key] = arguments[i][key];
                        }
                    }
                }
            }
        }
        return result;
    }

};
