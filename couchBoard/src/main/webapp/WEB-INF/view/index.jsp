<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="EUC-KR">
<script src='//unpkg.com/jquery@3/dist/jquery.min.js'></script>
<!-- 합쳐지고 최소화된 최신 CSS -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap.min.css">
<!-- 합쳐지고 최소화된 최신 자바스크립트 -->
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/js/bootstrap.min.js"></script>
<title>Insert title here</title>
<style>
 	.boardWrite table tr th, .boardDetail table tr th{
 		text-align:right;
 	}
 	thead tr th{
 		text-align:center;
 	}
 	textarea{
 		text-align:center;
 	}
 	
 	html, body{
		height:100%;
		margin:5px;
		text-align:center;
 	}
 	
 	textarea {
    white-space: normal;
    text-align: justify;
    text-align-last: left;
    white-space: pre;
}
</style>
<%
	session.setAttribute("username", "test123");
%>
<script>
	
	let tb = new Array();
	
	window.onload = function(){
		boardListView();
	}

	
	function boardListView(){
		$.ajax({
			url:'/boards',
			type:'get',
			success:function(data){
				
				// 페이지번호 생성
				let num = data.length/10;
				$('#pagination').empty();
				for(let i=0;i<num;i++){
					$('#pagination').append('<li class="page-item"><a class="page-link" onclick="pageMove('+i+')">'+(i+1)+'</a></li>');
				}
				tb = [];
				
				$.each(data, function(idx,val) {
					tb.push({boardNum:val.boardNum,username:val.username, title:val.title, writeTime:val.writeTime,hits:val.hits,id:val.id});
				});
				
				pageMove(0);
			}
		});
	}
	
	function pageMove(pageNum){
		
		var html = '';
		let bufferTb = [];
		if(pageNum==0)
			bufferTb = tb.slice(pageNum,pageNum+10);
		else
			bufferTb = tb.slice((pageNum*10),(pageNum+1)*10);
		
		$.each(bufferTb, function(idx,val) {
			html += '<tr><td>'+val.boardNum+'</td>';
			html += '<td>'+val.title+'</td>';
			html += '<td>'+val.username+'</td>';
			html += '<td>'+val.writeTime+'</td>';
			html += '<td>'+val.hits+'</td>';
			html += '<input type="hidden" name=boardId id=boardId value='+val.id+'></tr>';
		});
		$('#boardListTbody').empty();
		$('#boardListTbody').append(html);
		
		
		// boardDetailButton
		$("#boardListTbody tr").click(function(){
			let boardId = $(this).find("[name=boardId]").val();
			let boardNum = $(this).children().eq(0).text();
			// 조회수 Update
			$.ajax({
				url:'/boards/'+boardId+'/'+boardNum,
				type:'PUT',
				contentType:'application/json',
				success:function(data){
					boardDetailView(boardId,boardNum);
				}
			}); 
			
		});
	}
	
	function boardDetailView(boardId,boardNum){
		$.ajax({
			url:'/boards/'+boardId,
			type:'POST',
			success:function(data){
				$('.boardList').css('display','none');
				$('#firstButton').css('display','none');
				$('.boardDetail').css('display','inline-block');
				
				$('#detailUsernameValue').text(data.username);
				$('#detailTitleValue').text(data.title);
				$('#detailContentValue').text(data.content);
				$('#detailHitsValue').text(data.hits);
				$('#detailWriteTimeValue').text(data.writeTime);
				
				$('#detailBoardId').val(data.id);
				$('#detailBoardNum').val(data.boardNum);
				
				if($('#detailUsernameValue').text()=='<%=session.getAttribute("username")%>'){
					let btnListHtml = '';
					btnListHtml += '<button style=margin:5px; class="btn btn-primary pull-right" type=button onclick="boardUpdateButton()">수정</button>';
					btnListHtml += '<button style=margin:5px; class="btn btn-primary pull-right" type=button onclick="boardDeleteButton()">삭제</button>';
					$('#btnList').prepend(btnListHtml);
				}
			},
			error:function(request,status,error){
				alert(error);
			}
		});
	}
	
	function boardWriteView(){
		$('.boardList').css('display','none');
		$('.boardWrite').css('display','inline-block');
		$('#firstButton').css('display','none');
		$('.usernameValue').text('<%=session.getAttribute("username") %>');
		$('#actionValue').val(0);
	}
	
	function boardWriteButton(){
		
		$('#username').val($('.usernameValue').text());
		let data = serializeObject();
		
		if(data.title.trim().length==0 || data.content.trim().length==0){
			alert('빈칸이 없어야 합니다.');
			return;
		}
		
		if($('#actionValue').val()==1){
			boardUpdateAction(data);
			return;
		}
		delete data.updateBoardId;

	 	$.ajax({
			url:'/boards',
			type:'POST',
			contentType:'application/json',
			data:JSON.stringify(data),
			success:function(data){
				
				alert(data);
				location.reload();
			},
			error:function(request,status,error){
				alert(error);
			}
			
		});  
	}
	
	function boardDeleteButton(){
		
		if(!confirm('삭제하시겠습니까?')){
			return false;
		}
		
		let bufBoardId = $('#detailBoardId').val();
		let bufBoardNum = $('#detailBoardNum').val();
		let userObject = {username:$('#detailUsernameValue').text()};
		
		
		$.ajax({
			url:'/boards/'+bufBoardId+'/'+bufBoardNum,
			type:'DELETE',
			contentType:'application/json',
			data:JSON.stringify(userObject),
			success:function(data){
				
				alert(data);
				location.reload();
			},
			error:function(request,status,error){
				console.log(request);
				console.log(status);
				alert(error);
			}
			
		}); 
	}
	
	function boardUpdateButton(){
		$('.boardDetail').css('display','none');
		$('.boardWrite').css('display','inline-block');
		
		$('.usernameValue').text($('#detailUsernameValue').text());
		$('#title').val($('#detailTitleValue').text());
		$('#content').text($('#detailContentValue').text());
		$('#updateBoardId').val($('#detailBoardId').val());
		$('#updateBoardNum').val($('#detailBoardNum').val());
		
		$('#actionValue').val('1');
		
	}
	
	function boardUpdateAction(data){
		let boardId = data.updateBoardId;
		delete data.updateBoardId;
  	 	$.ajax({
			url:'/boards/'+boardId,
			type:'PUT',
			contentType:'application/json',
			data:JSON.stringify(data),
			success:function(data){
				alert(data);
				location.reload();
			},
			error:function(request,status,error){
				console.log(error);
				console.log(request);
				console.log(status);
			}
	 	}); 
	}

	// Form > JsonObject
	function serializeObject(){
		var o = {};
		   var a = $('#boardWriteForm').serializeArray();
		   $.each(a, function() {
		       if (o[this.name]) {
		           if (!o[this.name].push) {
		               o[this.name] = [o[this.name]];
		           }
		           o[this.name].push(this.value || '');
		       } else {
		           o[this.name] = this.value || '';
		       }
		   });
		   return o;
	}

</script>
</head>
<body>

<button type=button id=firstButton onclick="boardListView()" >게시판</button>

<!--  board list view --> 
<div id=container class=boardList style="display:block;width:100%;text-align:center;">
	<table class="table table-striped table-hover" id="boardListTable" >
		<colgroup>
		    <col width="10%" />
		    <col width="30%" />
		    <col width="15%" />
		    <col width="15%" />
		    <col width="10%" />
		</colgroup>
		<thead>
			<tr>
				<th>글 번호</th>
				<th>제목</th>
				<th>작성자</th>
				<th>작성날짜</th>
				<th>조회수</th>
			</tr>
		</thead>
		<tbody id='boardListTbody'>
			<tr>
			</tr>
		</tbody>
	</table>
	<button class="btn btn-primary pull-right" type=button onclick="boardWriteView()">글 쓰기</button>
	<ul class="pagination" id='pagination'>
	</ul>
</div>


<!--  board write view --> 
<div id=container class=boardWrite style="display:none;border:1px solid black;margin-top:5%;text-align:center;">
	<form name=boardWriteForm id=boardWriteForm>
		<table class="table table-striped" >
			<colgroup>
			    <col width="15%" />
			    <col width="30%" />
			</colgroup>
			<tr>
				<th>작성자<input type="hidden" name=username id=username></th>
				<td class="usernameValue"></td>
			</tr>
			<tr>
				<th>제목</th>
				<td><input type=text name=title id=title></td>
			</tr>
			<tr>
				<th colspan=2 style=text-align:center;>내용</th>
			</tr>
				
		</table>
		<textarea id=content name=content class="form-control col-sm-5" rows="10"></textarea>
		<input type="hidden" name=updateBoardId id=updateBoardId>
		<button style=margin:5px; class="btn btn-primary pull-right" type=button onclick="boardWriteButton()">작성</button>
		<button style=margin:5px; class="btn btn-primary pull-right" type=button onclick="location.reload()">돌아가기</button>
	</form>
</div>

<!-- board Detail view -->
<div id=container class=boardDetail style="display:none;border:1px solid black;margin-top:5%;text-align:center;">
	<table class="table table-striped" >
		<colgroup>
		    <col width="15%" />
		    <col width="30%" />
		</colgroup>
		<tr>
			<th>작성자<input type="hidden" name=username id=username></th>
			<td id="detailUsernameValue"></td>
		</tr>
		<tr>
			<th>제목</th>
			<td id="detailTitleValue"></td>
		</tr>
		<tr>
			<th>작성날짜 </th>
			<td id="detailWriteTimeValue"></td>
		</tr>
		<tr>
			<th>조회수</th>
			<td id="detailHitsValue"></td>
		</tr>
		<tr>
			<th colspan=2 style=text-align:center;>내용</th>
		</tr>
	</table>
	<textarea id=detailContentValue name=detailContentValue class="form-control col-sm-5" rows="10" readonly ></textarea>
	<input type="hidden" name=detailBoardId id=detailBoardId>
	<input type="hidden" name=detailBoardNum id=detailBoardNum>
	<input type="hidden" name=actionValue id=actionValue>
	<div id=btnList>
		<button style=margin:5px; class="btn btn-primary pull-right" type=button onclick="location.reload()">돌아가기</button>
	</div>
</div>

</body>
</html>