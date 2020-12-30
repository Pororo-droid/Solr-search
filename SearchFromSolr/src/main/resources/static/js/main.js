function search(){
    var anyWord = document.getElementsByName('anyWord');
    let mustWord = document.getElementsByName('mustWord');
    let class_ = document.getElementsByName('class')
    let loc = "search?anyWord="+anyWord[0].value+"&mustWord="+mustWord[0].value+"&class_="+class_[0].value;
    location.href = loc;
}
