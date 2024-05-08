import { Component } from '@angular/core';
import { NavbarComponent } from '../navbar/navbar.component';
import { PunishService } from '../../services/punish.service';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CookieService } from 'ngx-cookie-service';
import { validatorRole } from '../../validators/validator';

@Component({
  selector: 'app-admin-panel',
  standalone: true,
  imports: [NavbarComponent,ReactiveFormsModule],
  providers: [PunishService],
  templateUrl: './admin-panel.component.html',
  styleUrl: './admin-panel.component.css'
})
export class AdminPanelComponent {
  punishForm: FormGroup;
  assignRoleForm:FormGroup;
  unpunishForm: FormGroup;
  user=JSON.parse(this.cookieService.get('token')).username;
  constructor(private punishService: PunishService,private route:Router,private fb:FormBuilder,private cookieService:CookieService) {
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
  ban() {
    this.punishService.ban(this.punishForm.value).subscribe((data) => {
      alert(data);
    });
  }
  unban() {
    this.punishService.unban(this.unpunishForm.value).subscribe((data) => {
      alert(data.message);
    });
  }
  mute() {
    this.punishService.mute(this.punishForm.value).subscribe((data) => {
      alert(data);
    });
  }
  unmute() {
    this.punishService.unmute(this.unpunishForm.value).subscribe((data) => {
      console.log(data);
      alert(data.message);
    });
  }
  warn() {
    this.punishService.warn(this.punishForm.value).subscribe((data) => {
      alert(data);
    });
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
