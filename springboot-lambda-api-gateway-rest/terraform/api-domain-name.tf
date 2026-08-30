#
#
#
resource "aws_acm_certificate" "api" {
    domain_name       = var.domain_name
    validation_method = "DNS"
}

resource "aws_acm_certificate_validation" "api" {
    certificate_arn         = aws_acm_certificate.api.arn
    validation_record_fqdns = [for record in aws_route53_record.api_validation : record.fqdn]
}

data "aws_route53_zone" "public" {
    name         = var.domain_name
    private_zone = false
}

resource "aws_route53_record" "api_validation" {
    for_each = {
        for dvo in aws_acm_certificate.api.domain_validation_options : dvo.domain_name => {
            name   = dvo.resource_record_name
            record = dvo.resource_record_value
            type   = dvo.resource_record_type
        }
    }

    allow_overwrite = true
    name            = each.value.name
    records         = [each.value.record]
    ttl             = 60
    type            = each.value.type
    zone_id         = data.aws_route53_zone.public.zone_id
}

resource "aws_route53_record" "api" {
    name    = aws_api_gateway_domain_name.api.domain_name
    type    = "A"
    zone_id = data.aws_route53_zone.public.zone_id

    alias {
        name                   = aws_api_gateway_domain_name.api.regional_domain_name
        zone_id                = aws_api_gateway_domain_name.api.regional_zone_id
        evaluate_target_health = false
    }
}

resource "aws_api_gateway_domain_name" "api" {
    domain_name = var.domain_name
    regional_certificate_arn = aws_acm_certificate.api.arn
    security_policy = "TLS_1_2"

    endpoint_configuration {
        types           = ["REGIONAL"]
    }

    depends_on = [aws_acm_certificate_validation.api]
}

resource "aws_api_gateway_base_path_mapping" "api" {
    api_id      = aws_api_gateway_rest_api.my_api.id
    domain_name = aws_api_gateway_domain_name.api.id
    stage_name  = aws_api_gateway_stage.stage.stage_name
}
