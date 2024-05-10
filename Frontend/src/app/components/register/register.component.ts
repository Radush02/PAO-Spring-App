import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { UserService } from '../../services/user.service';
import { Router, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  imports: [CommonModule,RouterOutlet,HttpClientModule,ReactiveFormsModule],
  providers:[UserService],
  styleUrls: ['./register.component.css'],
  standalone:true
})
export class RegisterComponent {
  registerForm: FormGroup;
  errorMessage="";
  constructor(private http: HttpClient,private register:UserService,private router:Router,private fb:FormBuilder) { 
    this.registerForm=this.fb.group
      ({
        username: ['', Validators.required],
        name: ['', Validators.required],
        email: ['', [Validators.required, Validators.email]],
        password: ['', Validators.required]
      });

  }

  onSubmit() {
    this.register.register(this.registerForm.value).subscribe((response: any) => {
      this.router.navigate(['/login']);

    }, (error: any) => {
      console.error(error);
      console.log(this.registerForm.value)
      this.errorMessage = error.error;
    });
  }
  login(){
    this.router.navigate(['/login']);
  }
}
