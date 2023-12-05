provider "aws" {
  region = "us-east-1"
}
resource "aws_instance" "TrinoDaninoInstance" {
  ami                    = "ami-0fc5d935ebf8bc3bc"
  instance_type          = "t2.micro"
  key_name      = "shunter_ubuntu_key"  # Le nom de la clé telle qu'enregistrée dans AWS EC2

  tags = {
    Name = "TrinoDaninoInstance"
  }
}
