/**
 * 提交回复
 */
//添加一级评论
function parentComment() {
    var questionId = $("#question_id").val();
    var content = $("#comment_content").val();
    comment2Target(questionId, 1, content);
}

//添加二级评论
function sonComment(e) {
    var commentId = e.getAttribute("data-id");  //获取父评论id
    var content = $("#input-" + commentId).val();
    comment2Target(commentId, 2, content);
}

function comment2Target(targetId, type, content) {
    if (!content) {
        alert("不能回复空内容~~~");
        return;
    }

    $.ajax({
        type: "POST",
        url: "/comment",
        contentType: 'application/json',
        data: JSON.stringify({
            "parentId": targetId,
            "content": content,
            "type": type
        }),
        success: function (response) {
            if (response.code == 200) {
                window.location.reload();
            } else {
                if(response.code == 2003){
                    if(confirm(response.message)){
                        window.open("https://gitee.com/oauth/authorize?client_id=1b3234a7df41c37c0f0af9abe08de5b96f6d0578fcad8aed24b63321bdd70251&redirect_uri=http://localhost:8080/GiteeCallback&response_type=code");
                        window.localStorage.setItem("closable", "true");
                    }
                }
            }
        },
        dataType: "json"
    });
}

/**
 * 展开二级评论
 */
function collapseComments(e) {
    var id = e.getAttribute("data-id");
    var comments = $("#comment-" + id);

    // 获取一下二级评论的展开状态
    var collapse = e.getAttribute("data-collapse");
    if (collapse) {
        // 折叠二级评论
        comments.removeClass("in");
        // 标记二级评论展开状态
        e.removeAttribute("data-collapse");
        e.classList.remove("active");
    } else {
        var subCommentContainer = $("#comment-" + id);
        if (subCommentContainer.children().length != 1) {
            //展开二级评论
            comments.addClass("in");
            // 标记二级评论展开状态
            e.setAttribute("data-collapse", "in");
            e.classList.add("active");
        } else {
            $.getJSON("/comment/" + id, function (data) {
                //回调函数，显示每个子评论
                $.each(data.data.reverse(), function (index, comment) {
                    var mediaLeftElement = $("<div/>", {
                        "class": "media-left"
                    }).append($("<img/>", {
                        "class": "media-object img-rounded",
                        "src": comment.user.avatarUrl
                    }));

                    var mediaBodyElement = $("<div/>", {
                        "class": "media-body"
                    }).append($("<h5/>", {
                        "class": "media-heading",
                        "html": comment.user.name
                    })).append($("<div/>", {
                        "html": comment.content
                    })).append($("<div/>", {
                        "class": "menu"
                    }).append($("<span/>", {
                        "class": "pull-right",
                        "html": moment(comment.gmtCreate).format('YYYY-MM-DD')
                    })));

                    var mediaElement = $("<div/>", {
                        "class": "media"
                    }).append(mediaLeftElement).append(mediaBodyElement);

                    var commentElement = $("<div/>", {
                        "class": "col-lg-12 col-md-12 col-sm-12 col-xs-12 comments"
                    }).append(mediaElement);
                    subCommentContainer.prepend(commentElement);
                });
                //展开二级评论
                comments.addClass("in");
                // 标记二级评论展开状态
                e.setAttribute("data-collapse", "in");
                e.classList.add("active");
            });
        }
    }
}

function showSelectTag() {
    $("#select-tag").show();
}

function selectTag(e) {
    var value = e.getAttribute("data-tag");
    var previous = $("#tag").val();
    if (previous) {
        var index = 0;
        var appear = false; //记录value是否已经作为一个独立的标签出现过
        while (true) {
            index = previous.indexOf(value, index); //value字符串在previous中第一次出现的位置
            if (index == -1) break; //-1代表没有出现，则直接添加到previous中
            //判断previous中出现的value是否是另一个标签的一部分,如java是javaScript的一部分
            //即value的前一个和后一个字符都是逗号","或者没有字符时，才说明value作为一个独立的标签在previous中出现过，而不只是一部分
            if ((index == 0 || previous.charAt(index - 1) == ",")
                && (index + value.length == previous.length || previous.charAt(index + value.length) == ",")) {
                appear = true;
                break;
            }
            index++;
            //如previous中有javaScript，再添加java，明显上面的终止条件都不符合，index为javaScript中j出现的位置，则下一次查找从a开始
        }
        if (!appear) {
            //若value没有作为一个独立的标签出现过
            $("#tag").val(previous + ',' + value);
        }
    }
    else {
        $("#tag").val(value);
    }
}

//问题点赞
function questionThumbsUp() {
    var questionId = $("#question_id").val();   //获取问题id
    like2Target(questionId, 1, 0);
}

//问题取消点赞
function questionCancelThumbsUp() {
    var questionId = $("#question_id").val();
    like2Target(questionId, 1, 1);
}

//评论点赞
function commentThumbsUp(e) {
    var commentId = e.getAttribute("comment-id");  //获取评论id
    like2Target(commentId, 2, 0);
}

//评论取消点赞
function commentCancelThumbsUp(e) {
    var commentId = e.getAttribute("comment-id");
    like2Target(commentId, 2, 1);
}

function like2Target(targetId, type, state) {
    $.ajax({
        type: "POST",
        url: "/like",
        contentType: 'application/json',
        data: JSON.stringify({
            "parentId": targetId,
            "type": type,
            "state": state
        }),
        success: function (response) {
            if (response.code == 200) {
                window.location.reload();
            } else {
                if(response.code == 2003){
                    if(confirm(response.message)){
                        window.open("https://gitee.com/oauth/authorize?client_id=1b3234a7df41c37c0f0af9abe08de5b96f6d0578fcad8aed24b63321bdd70251&redirect_uri=http://localhost:8080/GiteeCallback&response_type=code");
                        window.localStorage.setItem("closable", "true");
                    }
                }
            }
        },
        dataType: "json"
    });
}