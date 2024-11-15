
// 전체 학생정보조회 
document.addEventListener('DOMContentLoaded', function() {
    fetch('/api/students')
        .then(response => response.json())
        .then(data => {
            const tbody = document.querySelector('#student tbody');
            tbody.innerHTML = '';       // 테이블 내용 초기화
            data.forEach(student => {   // 각 학생에 대해 테이블 행을 생성
                const row = document.createElement('tr');
                row.innerHTML = `
                <td>${student.id}"></td>
                <td>${student.name}"></td>
                <td>${student.major}"></td>
                <td>${student.phone}"></td>
                <td>${student.address}"></td>
                <td>
                    <button onclick="editStudent(${student.id})">수정</button>
                    <button onclick="deleteStudent(${student.id})">삭제</button>
                </td>
                `;
                tbody.appendChild(row);     // 테이블에 행 추가
            })

        });
    });

// 학생추가 (studentForm 제출)  사용자가 입력한 데이터를 BE로 전송 하여 추가
document.getElementById('studentForm').addEventListener('submit', function(e) {
    e.preventDefault();
    const name = document.getElementById('name').value;
    const major = document.getElementById('major').value;
    const phone = document.getElementById('phone').value;
    const address = document.getElementById('address').value;

    fetch('/api/students', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ name, major, phone, address })
    })
    .then(response => response.json())
    .then(data => {
        console.log("Student added successfully", data);
        location.reload();  // 페이지 새로고침
    });
});

// 학생 수정 (updateForm 폼 제출)
document.getElementById('updateForm').addEventListener('submit', function(e) {
    e.preventDefault();
    const id = document.getElementById('updateId').value;
    const name = document.getElementById('updateName').value;
    const major = document.getElementById('updateMajor').value;
    const phone = document.getElementById('updatePhone').value;
    const address = document.getElementById('updateAddr').value;

    fetch(`/api/students/${id}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ name, major, phone, address })
    })
    .then(response => response.json())
    .then(data => {
        console.log("Student updated successfully", data);
        location.reload();  // 페이지 새로고침
    });
});


// 학생 삭제 (deleteStudent 함수)
window.deleteStudent = function(id){
    fetch(`/api/students/${id}`, { method: 'DELETE'})
        .then(response = response.json())
        .then(data => {
            console.log("Student deleted successfully", data);
            location.reload();
        });
};

// 학생 편집 (editStudent 함수)
window.editStudent = function(id) {
    fetch(`/api/students/${id}`)
        .then(response => response.json())
        .then(student => {
            document.getElementById('updateId').value = student.id;
            document.getElementById('updateName').value = student.name;
            document.getElementById('updateMajor').value = student.major;
            document.getElementById('updatePhone').value = student.phone;
            document.getElementById('updateAddr').value = student.address;
            document.getElementById('updateForm').style.display = 'block';
        });
};


