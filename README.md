项目中遇到的两个列表数据联动，用传统方式实现 UI 和数据会互相依赖，比较难梳理清楚关系，不利于维护。
尝试用 MVVM 来简化复杂数据联动下的依赖关系

MarkActivity：只承担 View 的职责
- 根据数据渲染 UI
- 将用户操作通知给 ViewModel

MarkViewModel：只负责管理数据，不关心 UI 做了什么

![screenshot](Screenshot.png)
