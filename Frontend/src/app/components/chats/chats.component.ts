import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { CookieService } from 'ngx-cookie-service';
import { ChatService } from '../../services/chat.service';
import { NavbarComponent } from '../navbar/navbar.component';

@Component({
  selector: 'app-chats',
  standalone: true,
  imports: [CommonModule,RouterOutlet,HttpClientModule,ReactiveFormsModule,NavbarComponent],
  providers: [ChatService],
  templateUrl: './chats.component.html',
  styleUrl: './chats.component.css'
})
export class ChatsComponent {
  chatForm: FormGroup;
  user=JSON.parse(this.cookieService.get('token')).username;
  receiver='';
  messages:MessageDTO[] = [];
  loaded=false;
  constructor(private chatService:ChatService,private router: Router,private fb: FormBuilder,private cookieService: CookieService,
    private params: ActivatedRoute
  ) {
    this.chatForm=this.fb.group({
      username: [this.user],
      message: [null,Validators.required]
    });
    this.params.queryParams.subscribe(params => {
      this.receiver = params['to'];
    });
  }
  send() {
    this.chatService.send(this.chatForm.value,this.receiver).subscribe((data) => {
      console.log(data);
      location.reload();
    });
  }
  export(){
    this.chatService.export({requester:this.user,receiver:this.receiver,sender:this.user}).subscribe(
      (data) => {
        const contentDispositionHeader = data.headers.get('Content-Disposition');
        const filename = contentDispositionHeader
          ? contentDispositionHeader.split(';')[1].trim().split('=')[1].replace(/"/g, '')
          : 'contact_admin_issue.json';
        const blob = new Blob([data.body], { type: 'application/json' });
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = filename;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);
      },
      (error) => {
        console.log(error.error);
      }
    );
  }
  sentMessages() {
    this.chatService.receive(this.user,this.receiver).subscribe((data:MessageDTO[]) => {
      for (let i = 0; i < data.length; i++) {
        const dateObject = new Date(data[i].date)
        const formattedTime = dateObject.getDate() + ' ' + dateObject.toLocaleString('default', { month: 'short' }) + ' ' + dateObject.getFullYear() + ' ' + dateObject.getHours() + ':' + dateObject.getMinutes();
        data[i].date = formattedTime;
      }
      this.messages=data;
      console.log(this.messages);
    });
  }
  ngOnInit(){
    this.chatForm=this.fb.group({
      senderName: [this.user],
      message: [null,Validators.required]
    });
    this.sentMessages();
    this.loaded=true;
  }
}
/*
public class MessageDTO {
    private String message;
    private String senderName;
    private Date date;
}

*/
interface MessageDTO {
  message: string;
  senderName: string;
  date: string;
}
