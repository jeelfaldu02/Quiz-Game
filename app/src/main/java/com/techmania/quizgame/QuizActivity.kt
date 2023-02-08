package com.techmania.quizgame

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.techmania.quizgame.databinding.ActivityQuizBinding
import kotlin.random.Random

class QuizActivity : AppCompatActivity() {

    lateinit var quizBinding:ActivityQuizBinding

    val database=FirebaseDatabase.getInstance()
    val databaseReference=database.reference.child("questions")

    var question=""
    var answerA=""
    var answerB=""
    var answerC=""
    var answerD=""
    var correctAnswer=""
    var questionCount=0
    var questionNumber=0
    var userAnswer=""
    var userCorrect=0
    var userWrong=0

    lateinit var timer:CountDownTimer
    private val totalTime=25000L
    var timerContinue=false
    var leftTime=totalTime

    val auth = FirebaseAuth.getInstance()
    val user=auth.currentUser
    val scoreRef=database.reference

    val questions=HashSet<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        quizBinding=ActivityQuizBinding.inflate(layoutInflater)
        val view=quizBinding.root
        setContentView(view)

        do{
            val number = Random.nextInt(1,11)
            questions.add(number)
        }while (questions.size<5)

        gameLogic()

        quizBinding.buttonFinish.setOnClickListener {

            sendScore()

        }

        quizBinding.buttonNext.setOnClickListener {

            startTimer()
            gameLogic()

        }

        quizBinding.textViewA.setOnClickListener {

            userAnswer="a"

            pauseTimer()
            disableClickableOfOptions()

            if(correctAnswer==userAnswer){

                quizBinding.textViewA.setBackgroundColor(Color.GREEN)
                userCorrect++
                quizBinding.textViewCorrect.text=userCorrect.toString()

            }else{

                quizBinding.textViewA.setBackgroundColor(Color.RED)
                userWrong++
                quizBinding.textViewWrong.text=userWrong.toString()
                findAnswer()
            }
        }

        quizBinding.textViewB.setOnClickListener {

            userAnswer="b"

            pauseTimer()
            disableClickableOfOptions()

            if(correctAnswer==userAnswer){

                quizBinding.textViewB.setBackgroundColor(Color.GREEN)
                userCorrect++
                quizBinding.textViewCorrect.text=userCorrect.toString()

            }else{

                quizBinding.textViewB.setBackgroundColor(Color.RED)
                userWrong++
                quizBinding.textViewWrong.text=userWrong.toString()
                findAnswer()
            }

        }

        quizBinding.textViewC.setOnClickListener {

            userAnswer="c"

            pauseTimer()
            disableClickableOfOptions()

            if(correctAnswer==userAnswer){

                quizBinding.textViewC.setBackgroundColor(Color.GREEN)
                userCorrect++
                quizBinding.textViewCorrect.text=userCorrect.toString()

            }else{

                quizBinding.textViewC.setBackgroundColor(Color.RED)
                userWrong++
                quizBinding.textViewWrong.text=userWrong.toString()
                findAnswer()
            }

        }

        quizBinding.textViewD.setOnClickListener {

            userAnswer="d"

            pauseTimer()
            disableClickableOfOptions()

            if(correctAnswer==userAnswer){

                quizBinding.textViewD.setBackgroundColor(Color.GREEN)
                userCorrect++
                quizBinding.textViewCorrect.text=userCorrect.toString()

            }else{

                quizBinding.textViewD.setBackgroundColor(Color.RED)
                userWrong++
                quizBinding.textViewWrong.text=userWrong.toString()
                findAnswer()
            }

        }

    }

    private fun gameLogic(){

        restoreOptions()

        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                questionCount=snapshot.childrenCount.toInt()

                if(questionNumber < questions.size){

                    question=snapshot.child(questions.elementAt(questionNumber).toString()).child("q").value.toString()
                    answerA=snapshot.child(questions.elementAt(questionNumber).toString()).child("a").value.toString()
                    answerB=snapshot.child(questions.elementAt(questionNumber).toString()).child("b").value.toString()
                    answerC=snapshot.child(questions.elementAt(questionNumber).toString()).child("c").value.toString()
                    answerD=snapshot.child(questions.elementAt(questionNumber).toString()).child("d").value.toString()
                    correctAnswer=snapshot.child(questions.elementAt(questionNumber).toString()).child("answer").value.toString()

                    quizBinding.textViewQuestion.text=question
                    quizBinding.textViewA.text=answerA
                    quizBinding.textViewB.text=answerB
                    quizBinding.textViewC.text=answerC
                    quizBinding.textViewD.text=answerD

                    quizBinding.progressBarQuiz.visibility=View.INVISIBLE
                    quizBinding.linearLayoutButtons.visibility=View.VISIBLE
                    quizBinding.linearLayoutInfo.visibility=View.VISIBLE
                    quizBinding.linearLayoutQuestion.visibility=View.VISIBLE

                    startTimer()
                }else{

                    val dialogMessage=AlertDialog.Builder(this@QuizActivity)
                    dialogMessage.setTitle("Quiz Game")
                    dialogMessage.setMessage("Congratulations!!!\nYou have answered all the questions. Do you want to see the result?")
                    dialogMessage.setCancelable(false)
                    dialogMessage.setPositiveButton("See result"){dialogWindow,position->

                        sendScore()

                    }
                    dialogMessage.setNegativeButton("Play again"){dialogWindow,position->
                        val intent=Intent(this@QuizActivity,MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    dialogMessage.create().show()

                }

                questionNumber++

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext,error.message,Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun findAnswer(){
        when(correctAnswer){

            "a"-> quizBinding.textViewA.setBackgroundColor(Color.GREEN)
            "b"-> quizBinding.textViewB.setBackgroundColor(Color.GREEN)
            "c"-> quizBinding.textViewC.setBackgroundColor(Color.GREEN)
            "d"-> quizBinding.textViewD.setBackgroundColor(Color.GREEN)
        }
    }

    private fun disableClickableOfOptions(){
        quizBinding.textViewA.isClickable=false
        quizBinding.textViewB.isClickable=false
        quizBinding.textViewC.isClickable=false
        quizBinding.textViewD.isClickable=false
    }

    private fun restoreOptions(){

        quizBinding.textViewA.isClickable=true
        quizBinding.textViewB.isClickable=true
        quizBinding.textViewC.isClickable=true
        quizBinding.textViewD.isClickable=true

        quizBinding.textViewA.setBackgroundColor(Color.WHITE)
        quizBinding.textViewB.setBackgroundColor(Color.WHITE)
        quizBinding.textViewC.setBackgroundColor(Color.WHITE)
        quizBinding.textViewD.setBackgroundColor(Color.WHITE)
    }

    private fun startTimer(){
        timer = object : CountDownTimer(leftTime,1000){
            override fun onTick(millisUntilFinished: Long) {

                leftTime=millisUntilFinished
                updateCountDownText()
            }

            override fun onFinish() {

                disableClickableOfOptions()
                resetTimer()
                updateCountDownText()
                quizBinding.textViewQuestion.text="Sorry, your time is up! Continue to next question."
                timerContinue=false
            }

        }.start()

        timerContinue=true
    }

    private fun updateCountDownText(){

        val remainingTime:Int=(leftTime/1000).toInt()
        quizBinding.textViewTime.text=remainingTime.toString()
    }

    private fun pauseTimer(){

        timer.cancel()
        timerContinue=false
    }

    private fun resetTimer(){

        pauseTimer()
        leftTime=totalTime
        updateCountDownText()
    }

    fun sendScore(){
        user?.let {

            val userUID=it.uid
            scoreRef.child("scores").child(userUID).child("correct").setValue(userCorrect)
            scoreRef.child("scores").child(userUID).child("wrong").setValue(userWrong).addOnSuccessListener {

                Toast.makeText(applicationContext,"Scores sent to database successfully",Toast.LENGTH_SHORT).show()
                val intent=Intent(this@QuizActivity,ResultActivity::class.java)
                startActivity(intent)
                finish()
            }


        }
    }
}