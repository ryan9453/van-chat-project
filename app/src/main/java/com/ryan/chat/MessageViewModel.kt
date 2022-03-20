package com.ryan.chat

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.WebSocket

class MessageViewModel :ViewModel() {
    val blockStringList = listOf<Char>('?','~','!','#','$','%','^','&','*','(',')','_','-','+','=','?','<','>','.','—','，','。','/','\\','|','《','》','？',';',':','：','\'','‘','；','“',)
    val messages = MutableLiveData<String>()

    fun getMessages(singleMessage: String) {
//        val singleMessageList = singleMessage.toCharArray()
        for (i in singleMessage) {
            if (blockStringList.contains(i)) {
                singleMessage.replace("$i", "")
            }
        }
        if (singleMessage != "") messages.postValue(singleMessage)
    }
}