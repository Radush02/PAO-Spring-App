import { Component } from '@angular/core';
import { NavbarComponent } from '../navbar/navbar.component';
import { PunishService } from '../../services/punish.service';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CookieService } from 'ngx-cookie-service';
import { validatorRole } from '../../validators/validator';
import { GameService } from '../../services/game.service';
import { ChatService } from '../../services/chat.service';

@Component({
  selector: 'app-admin-panel',
  standalone: true,
  imports: [NavbarComponent,ReactiveFormsModule],
  providers: [PunishService,GameService,ChatService],
  templateUrl: './admin-panel.component.html',
  styleUrl: './admin-panel.component.css'
})
export class AdminPanelComponent {
  punishForm: FormGroup;
  assignRoleForm:FormGroup;
  unpunishForm: FormGroup;
  user=JSON.parse(this.cookieService.get('token')).username;
  constructor(private punishService: PunishService,private route:Router,private fb:FormBuilder,private cookieService:CookieService,private gameService:GameService,private chatService:ChatService) {
    /*
    public class PunishDTO {
    private String username;
    private String admin;
    private String reason;
    @Setter private Date expiryDate;
}
    */
    this.punishForm=this.fb.group({
      username: [null,Validators.required],
      admin: [this.user],
      reason: [null,Validators.required],
      expiryDate: [null,Validators.required]
    });
    this.assignRoleForm=this.fb.group({
      username: [null,Validators.required],
      admin: [this.user],
      role: [null,Validators.required]
    });
    this.unpunishForm=this.fb.group({
      user: [null,Validators.required],
      admin: [this.user]
    });

  }
  uploadBackup(){
    const fileInput = document.getElementById('backupFile') as HTMLInputElement;
    const button = document.getElementById('chatBackup') as HTMLButtonElement;
    const sender = (document.getElementById('sender') as HTMLInputElement).value;
    const receiver = (document.getElementById('receiver') as HTMLInputElement).value;
    
    button.disabled = true;
    if (fileInput.files && fileInput.files.length > 0) {
      const file = fileInput.files[0];
      this.chatService.import({file:file,sender:sender,receiver:receiver,requester:this.user}).subscribe(response => {
        console.log(response);
        button.disabled=false;
        alert("Back-up incarcat cu succes");
      },error=>
        {
        console.error(error);
        alert(error.error.split(': ')[1]);
        button.disabled=false;
    });
    }
    else{
      button.disabled=false;
      alert('Incarca un fisier');
    }
  }
  ban() {
    this.punishService.ban(this.punishForm.value).subscribe(
      (data) => {
        alert(`Userul ${this.punishForm.value.username} a fost banat pana la data de ${this.punishForm.value.expiryDate}.`);
      },
      (error) => {
        alert(error.error.split(': ')[1]);
      }
    );
  }
  unban() {
    this.punishService.unban(this.unpunishForm.value).subscribe(
      (data) => {
        alert(`Userul ${this.unpunishForm.value.user} a primit unban.`);
      },
      (error) => {
        alert(error.error.split(': ')[1]);
      }
    );
  }
  mute() {
    this.punishService.mute(this.punishForm.value).subscribe(
      (data) => {
        alert(`Userul ${this.punishForm.value.username} are mute pana la data de ${this.punishForm.value.expiryDate}.`);
      },
      (error) => {
        alert(error.error.split(': ')[1]);
      }
    );
  }
  unmute() {
    this.punishService.unmute(this.unpunishForm.value).subscribe(
      (data) => {
        alert(`Userul ${this.unpunishForm.value.user} a primit unmute.`);
      },
      (error) => {
        alert(error.error.split(': ')[1]);
      }
    );
  }
  warn() {
    this.punishService.warn(this.punishForm.value).subscribe(
      (data) => {
        alert(`Userul ${this.punishForm.value.username} a primit warn.`);
      },
      (error) => {
        alert(error.error.split(': ')[1]);
      }
    );
  }
  uploadGame(){
    const fileInput = document.getElementById('file') as HTMLInputElement;
    const button = document.getElementById('statistici') as HTMLButtonElement;
    const gameId = (document.getElementById('gameId') as HTMLInputElement).value;
    button.disabled = true;
    if (fileInput.files && fileInput.files.length > 0) {
      const file = fileInput.files[0];
      this.gameService.importMultiplayerGame(file,gameId).subscribe(response => {
        console.log(response);
        button.disabled=false;
        alert("Back-up incarcat cu succes");
      },error=>
        {
          console.error(error);
        alert(error.error.split(': ')[1]);
        button.disabled=false;
    });
    }
    else{
      button.disabled=false;
      alert('Incarca un fisier');
    }
  }
  getLogs() {
    this.punishService.getLogs(this.user).subscribe((data) => {
      const blob = new Blob([data.body], { type: 'text/csv' });
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      const time = new Date().toISOString();
      a.download = 'logs_'+time+'.csv';
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      window.URL.revokeObjectURL(url);
    });
  }
  assignRole() {
    console.log(this.assignRoleForm.value)
    this.punishService.assignRole(this.assignRoleForm.value).subscribe((data) => {
      alert(data);
    });
  }
}
